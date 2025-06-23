// 쿠키에서 사용자 정보 읽기
const getUserInfoFromCookie = () => {
    const cookies = document.cookie.split(';');
    const employeeInfoCookie = cookies.find(cookie => 
        cookie.trim().startsWith('EMPLOYEE_INFO=')
    );
    
    if (!employeeInfoCookie) {
        console.log('EMPLOYEE_INFO 쿠키를 찾을 수 없습니다.');
        return null;
    }
    
    try {
        const encodedUserInfo = decodeURIComponent(employeeInfoCookie.split('=')[1]);
        // Base64 디코딩
        const binaryString = atob(encodedUserInfo);
        // UTF-8 디코딩
        const bytes = new Uint8Array(binaryString.length);
        for (let i = 0; i < binaryString.length; i++) {
            bytes[i] = binaryString.charCodeAt(i);
        }
        const userInfoJson = new TextDecoder('utf-8').decode(bytes);
        const userInfo = JSON.parse(userInfoJson);
        console.log('쿠키에서 읽은 사용자 정보:', userInfo);
        return userInfo;
    } catch (error) {
        console.error('쿠키 파싱 실패:', error);
        return null;
    }
};

// 세션 정보 가져오기 (쿠키에서 읽기)
const getSessionInfo = () => {
    const userInfo = getUserInfoFromCookie();
    
    if (!userInfo) {
        console.log('세션 정보를 가져올 수 없습니다.');
        return null;
    }
    
    return userInfo;
};

// 로그인 사용자 정보 - 쿠키에서 읽어오기
window.user = (() => {
    const userInfo = getUserInfoFromCookie();
    if (userInfo) {
        return {
            id: userInfo.employeeId,
            name: userInfo.employeeName,
            authCode: userInfo.authorities ? userInfo.authorities.split(',')[0].replace('ROLE_', '') : 'ATH001'
        };
    }
    // 쿠키에서 정보를 읽을 수 없는 경우 기본값 반환
    return {
        id: "",
        name: "",
        authCode: "ATH001"
    };
})();

// 사용자 정보 표시
const displayUserInfo = () => {
    const userInfo = getSessionInfo();
    if (!userInfo) return;
    
    // 사용자 정보 표시
    const userNameElement = document.querySelector('.user__name');
    if (userNameElement) {
        userNameElement.textContent = `${userInfo.employeeName}(${userInfo.employeeId})`;
        console.log('사용자 정보 표시 완료:', userNameElement.textContent);
    }
};

// 전역으로 사용할 수 있도록 window 객체에 추가
window.getSessionInfo = getSessionInfo;
window.getUserInfoFromCookie = getUserInfoFromCookie;
window.displayUserInfo = displayUserInfo;

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