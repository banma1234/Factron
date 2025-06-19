// 로그인 사용자 정보
window.user = {
    id : "25060001",
    name : "홍길동",
    authCode : "ATH002",
};

// grid 초기화
window.initGrid = (gridEl, bodyHeight, columns) => {
    const Grid = tui.Grid;

    // 테마
    Grid.applyTheme('default',  {
        cell: {
            normal: {
                border: 'gray'
            },
            header: {
                background: 'gray',
                text: 'white',
                border: 'gray'
            },
            rowHeaders: {
                header: {
                    background: 'gray',
                    text: 'white'
                }
            }
        }
    });

    // 세팅
    return new Grid({
        el: gridEl,
        scrollX: false,
        scrollY: true,
        bodyHeight: bodyHeight,
        columns: columns,
    });
}

// 공통코드 목록 조회
window.getSysCodeList = async (mainCode)  =>  {
    const res = await fetch(`/api/sys/detail?mainCode=${mainCode}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    });
    return res.json();
}

// 셀렉박스 옵션 설정
window.setSelectBox = (mainCode, selectTagName) => {
    getSysCodeList(mainCode).then((data) => {
        const selectTag = document.querySelector(`select[name=${selectTagName}]`);

        if(data.status === 200){
            data.data.forEach((code) => {
                const optionElement = document.createElement("option");
                optionElement.value = code.detail_code;
                optionElement.textContent = code.name;

                if(selectTag){
                    selectTag.appendChild(optionElement);
                }

            });
        }else{
            alert("공통코드를 불러오는 데 실패했습니다!")
        }
    });
};

// 한국 시간 기준 오늘 날짜 구하기
window.getKoreaToday = () => {
    const now = new Date();
    now.setHours(now.getHours() + 9); // UTC+9
    return now.toISOString().slice(0, 10);
}