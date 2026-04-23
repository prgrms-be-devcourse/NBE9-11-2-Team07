import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 1,
    iterations: 1,
};

export default function () {
    const url = 'http://localhost:8080/api/v1/reservations/attempts';
    const payload = JSON.stringify({
        date: '2026-04-22',
        time: '15:00:00',
        guestCount: 1
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const requests = [];
    for (let i = 0; i < 100; i++) {
        requests.push(['POST', url, payload, params]);
    }

    console.log('100개의 동시 요청 실행 준비 완료');

    const responses = http.batch(requests);

    let successCount = 0;
    let failCount = 0;

    responses.forEach(res => {
        if (res.status === 200 || res.status === 201) {
            successCount++;
        } else {
            failCount++;
        }
    });

    console.log('\n================================');
    console.log(`✅ 성공한 요청: ${successCount}건`);
    console.log(`❌ 실패한 요청: ${failCount}건`);
    console.log('================================\n');

    check(successCount, {
        '동시성 제어 성공: 단 1건만 예약되어야 함': (val) => val === 1,
    });
}