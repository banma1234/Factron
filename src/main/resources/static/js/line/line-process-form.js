// 라인 공정 추가를 위한 초기화 및 이벤트 핸들링
const init = () => {
    setupEventListeners();

    // 부모 창에 준비 완료 메시지 전송
    if (window.opener) {
        window.opener.postMessage('lineProcessReady', '*');
    }
}

// 이벤트 리스너 설정
const setupEventListeners = () => {
    const confirmModal = new bootstrap.Modal(document.querySelector('.confirmModal'));
    const confirmAddBtn = document.querySelector('.confirmAddBtn');
    const confirmModalBtn = document.querySelector('.confirmModalBtn');
    const alertModal = new bootstrap.Modal(document.querySelector('.alertModal'));
    const alertBtn = document.querySelector('.alertBtn');

    // 추가 버튼 클릭 이벤트
    if (confirmAddBtn) {
        confirmAddBtn.addEventListener('click', () => {
            console.log("추가 버튼 클릭");

            // 선택된 공정 확인
            const selectedProcesses = window.getSelectedProcesses ? window.getSelectedProcesses() : [];
            console.log("선택된 공정:", selectedProcesses);

            if (selectedProcesses.length === 0) {
                alert('추가할 공정을 선택해주세요.');
                return;
            }

            confirmModal.show();
        });
    }

    // 취소 버튼 이벤트
    const cancelBtn = document.querySelector('.btn-secondary');
    if (cancelBtn) {
        cancelBtn.addEventListener('click', () => {
            console.log("취소 버튼 클릭");
            window.close();
        });
    }

    // 확인 모달 - 확인 버튼 클릭
    if (confirmModalBtn) {
        confirmModalBtn.addEventListener('click', async () => {
            console.log("확인 모달 버튼 클릭");

            try {
                const res = await saveData();

                confirmModal.hide();
                alertModal.show();
            } catch (error) {
                console.error('공정 추가 중 오류 발생:', error);
                alert('공정 추가 중 오류가 발생했습니다.');
                confirmModal.hide();
            }
        });
    }

    // 알림 모달 - 확인 버튼 클릭
    if (alertBtn) {
        alertBtn.addEventListener('click', () => {
            console.log("알림 버튼 클릭");

            alertModal.hide();

            // 부모 창에 리프레시 메시지 전송
            if (window.opener && !window.opener.closed) {
                window.opener.postMessage({
                    type: 'REFRESH_LINE_PROCESSES'
                }, '*');
            }

            window.close();
        });
    }
}

// 데이터 저장 함수
async function saveData() {
    const lineId = document.querySelector('input[name="lineId"]').value;

    // 선택된 공정 목록 가져오기 (search-disconnected-process 프래그먼트의 함수 활용)
    const selectedProcesses = window.getSelectedProcesses ? window.getSelectedProcesses() : [];

    // 선택된 공정 ID 추출
    const processIds = selectedProcesses.map(process => parseInt(process.processId));

    console.log("연결할 공정 IDs:", processIds);

    try {
        const response = await fetch('/api/line/connect-process', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'empId': user.id // 사용자 ID 헤더 추가
            },
            body: JSON.stringify({
                lineId: parseInt(lineId),
                processIds: processIds
            })
        });

        return response.json();
    } catch (error) {
        console.error('API 호출 오류:', error);
        throw error;
    }
}

// 부모 창에서 라인 정보 수신
window.addEventListener('message', function(event) {
    const data = event.data;
    console.log("메시지 수신:", data);

    // 라인 정보 설정
    if (data.lineId && data.lineName) {
        document.querySelector('input[name="lineId"]').value = data.lineId;
        document.querySelector('input[name="lineName"]').value = data.lineName;

        // description이 있을 경우 설정
        const descriptionElement = document.querySelector('textarea[name="description"]');
        if (descriptionElement && data.description !== undefined) {
            descriptionElement.value = data.description;
        }
    }
});

// 페이지 로드 시 실행
window.addEventListener('DOMContentLoaded', async () => {
    try {
        await init();
        console.log("페이지 초기화 완료");
    } catch (error) {
        console.error("초기화 오류:", error);
    }
});