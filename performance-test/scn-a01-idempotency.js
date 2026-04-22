import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 1,
    iterations: 1,
};

export default function () {
    const url = 'http://localhost:8080/api/v1/reservations/attempts';
    const payload = JSON.stringify({
        date: "2026-05-23",
        time: "18:00:00",
        guestCount: 2
    });
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    // 50개 동시 요청
    const requests = Array(50).fill(['POST', url, payload, params]);
    const responses = http.batch(requests);

    let successCount = 0;
    responses.forEach((res) => {
        console.log(`status: ${res.status}`);
        if (res.status === 202) successCount++;
    });

    check(successCount, {
        'Only one request should succeed': (c) => c === 1,
    });
}