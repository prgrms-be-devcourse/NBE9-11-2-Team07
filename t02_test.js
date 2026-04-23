import http from 'k6/http';
import crypto from 'k6/crypto';
import encoding from 'k6/encoding';
import { check, sleep } from 'k6';

const baseUrl = __ENV.BASE_URL || 'http://localhost:8080';
const userAId = __ENV.USER_A_ID || '550e8400-e29b-41d4-a716-446655440001';
const userBId = __ENV.USER_B_ID || '550e8400-e29b-41d4-a716-446655440005';

const userATokenEnv = __ENV.USER_A_TOKEN || '';
const userBTokenEnv = __ENV.USER_B_TOKEN || '';

const jwtSecret = __ENV.JWT_SECRET || 'thisIsATestSecretKeyForLocalDevelopment123456';
const accessTokenTtlSeconds = Number(__ENV.ACCESS_TOKEN_TTL_SECONDS || 1800);

const reservationId = __ENV.RESERVATION_ID || '550e8400-e29b-41d4-a716-446655440002';
const reservationDate = __ENV.RESERVATION_DATE || '2026-04-22';
const reservationTime = __ENV.RESERVATION_TIME || '16:00:00';

const attackerGuestCount = Number(__ENV.ATTACKER_GUEST_COUNT || 3);

export const options = {
    vus: 1,
    iterations: 1,
    thresholds: {
        checks: ['rate>=1.0'],
    },
};

function toBase64Url(value) {
    return encoding.b64encode(value, 'rawurl');
}

function createJwtToken(userId, role = 'USER') {
    const nowSeconds = Math.floor(Date.now() / 1000);
    const header = {
        alg: 'HS256',
        typ: 'JWT',
    };

    const payload = {
        userId,
        role,
        iat: nowSeconds,
        exp: nowSeconds + accessTokenTtlSeconds,
    };

    const encodedHeader = toBase64Url(JSON.stringify(header));
    const encodedPayload = toBase64Url(JSON.stringify(payload));
    const unsignedToken = `${encodedHeader}.${encodedPayload}`;

    const signatureBytes = crypto.hmac('sha256', jwtSecret, unsignedToken, 'binary');
    const encodedSignature = encoding.b64encode(signatureBytes, 'rawurl');

    return `${unsignedToken}.${encodedSignature}`;
}

function normalizeBearerToken(rawToken) {
    if (!rawToken) {
        return '';
    }

    if (rawToken.startsWith('Bearer ')) {
        return rawToken;
    }

    return `Bearer ${rawToken}`;
}

function buildAccessToken(rawToken, fallbackUserId) {
    if (rawToken) {
        return normalizeBearerToken(rawToken);
    }
    return normalizeBearerToken(createJwtToken(fallbackUserId));
}

const userAToken = buildAccessToken(userATokenEnv, userAId);
const userBToken = buildAccessToken(userBTokenEnv, userBId);

function createHeaders(token, rawUserId) {
    return {
        'Content-Type': 'application/json',
        'X-USER-ID': rawUserId,
    };
}

function parseJsonSafely(response) {
    try {
        return response.json();
    } catch (error) {
        return null;
    }
}

function assertRequiredEnv() {
    const missing = [];

    if (!reservationId) {
        missing.push('RESERVATION_ID');
    }

    if (!userAToken && !userAId) {
        missing.push('USER_A_TOKEN or USER_A_ID');
    }

    if (!userBToken && !userBId) {
        missing.push('USER_B_TOKEN or USER_B_ID');
    }

    if (!jwtSecret) {
        missing.push('JWT_SECRET');
    }

    if (missing.length > 0) {
        throw new Error(`필수 환경변수가 누락되었습니다: ${missing.join(', ')}`);
    }
}

function isUnauthorized(responseJson) {
    if (!responseJson) {
        return false;
    }
    return responseJson.resultCode === '401' || responseJson.msg === '로그인이 필요한 서비스입니다.';
}

export default function () {
    assertRequiredEnv();

    const updateUrl = `${baseUrl}/api/v1/my/reservations/${reservationId}`;
    const snatchUrl = `${baseUrl}/api/v1/reservations/attempts`;

    const updatePayload = JSON.stringify({
        date: reservationDate,
        time: reservationTime,
        guestCount: 2,
    });

    const snatchPayload = JSON.stringify({
        date: reservationDate,
        time: reservationTime,
        guestCount: attackerGuestCount,
    });

    console.log('사용자 A 예약 수정 시작 (4명 -> 2명)');
    const updateRes = http.patch(updateUrl, updatePayload, {
        headers: createHeaders(userAToken, userAId),
    });

    console.log('수정 중간 타이밍에 사용자 B가 자리 선점 시도');
    sleep(0.1);
    const snatchRes = http.post(snatchUrl, snatchPayload, {
        headers: createHeaders(userBToken, userBId),
    });

    const updateJson = parseJsonSafely(updateRes);
    const snatchJson = parseJsonSafely(snatchRes);

    const updateBody = updateRes.body ? updateRes.body.slice(0, 300) : '';
    const snatchBody = snatchRes.body ? snatchRes.body.slice(0, 300) : '';

    const updateOk = check(updateRes, {
        '[수정 요청] HTTP 상태가 정상이어야 함': (r) => [200, 201, 202, 409, 422, 429, 500].includes(r.status),
        '[수정 요청] 인증 실패(resultCode=401)가 아니어야 함': () => !isUnauthorized(updateJson),
    });

    const snatchBlocked = check(snatchRes, {
        '[선점 요청] HTTP 상태가 정상/차단 범위여야 함': (r) => [200, 201, 202, 409, 422, 429, 500].includes(r.status),
        '[선점 요청] 인증 실패(resultCode=401)가 아니어야 함': () => !isUnauthorized(snatchJson),
    });

    console.log('\n========== T-02 결과 ==========' );
    console.log(`수정 요청 상태: ${updateRes.status}`);
    console.log(`수정 요청 resultCode: ${updateJson ? updateJson.resultCode : 'N/A'}`);
    console.log(`선점 요청 상태: ${snatchRes.status}`);
    console.log(`선점 요청 resultCode: ${snatchJson ? snatchJson.resultCode : 'N/A'}`);
    console.log(`수정 요청 응답(앞 300자): ${updateBody}`);
    console.log(`선점 요청 응답(앞 300자): ${snatchBody}`);
    console.log(`체크 결과(update): ${updateOk}`);
    console.log(`체크 결과(snatch): ${snatchBlocked}`);
    console.log('===============================\n');
}