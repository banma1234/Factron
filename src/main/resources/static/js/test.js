// grid 초기화
const initGrid = () => {
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
        el: document.getElementById('grid'),
        scrollX: false,
        scrollY: false,
        minBodyHeight: 30,
        rowHeaders: ['rowNum'],
        columns: [
            {
                header: '번호',
                name: 'id',
                hidden: true
            },
            {
                header: '이름',
                name: 'name',
                align: 'center'
            },
            {
                header: '체크타입',
                name: 'chkType',
                align: 'center',
                formatter: (value) => {
                    if (value) {
                        const data = value.value;
                        return `${data ? '완료' : '취소'}`;
                    }
                    return "";
                }
            },
            {
                header: '생일',
                name: 'birth',
                // formatter: (value) => {
                //     if (value) {
                //         const data = value.value;
                //         return `${data[0]}-${data[1]}-${data[2]}`;
                //     }
                //     return "";
                // }
            },
            {
                header: '주소',
                name: 'address',
                align: 'center'
            },
            {
                header: '이미지',
                name: 'filePath',
                align: 'center',
                // formatter: (value) => {
                //     if (value.value) {
                //         const imageUrl = value.value;
                //         console.log("이미지경로:"+imageUrl)
                //         const absoluteImageUrl = `/test/uploads/${imageUrl}`;
                //         return `<img src="${absoluteImageUrl}" alt="이미지" style="max-width: 100px; max-height: 25px;">`;
                //     }
                //     return "";
                // }
            },
            {
                header: '등록일',
                name: 'regDate',
                formatter: (value) => {
                    if (value) {
                        const data = value.value;
                        return data.split('T')[0];
                    }
                    return "";
                }
            }
        ],
    });
}

const init = () => {
    // grid 초기 세팅
    const testGrid = initGrid();

    // 검색
    document.querySelector(".srhBtn").addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        // 조회
        getData().then(res => {
            testGrid.resetData(res.data); // grid에 세팅
        });
    }, false);

    // form 창 오픈
    testGrid.on('dblclick', (e) => {
        const rowKey = e.rowKey;
        const rowData = testGrid.getRow(rowKey);

        // 새 창에서 해당 ID를 기반으로 상세페이지 오픈
        if (rowData && rowData.id) {
            const popup = window.open('/test-form', '_blank', 'width=800,height=600');

            // 자식 창으로부터 'ready' 먼저 수신 후 postMessage 실행
            const messageHandler = (event) => {
                if (event.data === 'ready') {
                    popup.postMessage({
                        name: rowData.name,
                        age: rowData.id,
                        birth: rowData.birth,
                        regDate: rowData.regDate,
                        remark: rowData.address
                    }, "*");
                    window.removeEventListener("message", messageHandler);
                }
            };
            window.addEventListener("message", messageHandler);
        }
    });

    // 목록 조회
    window.getData = async function () {
        console.log("getData")
        // validation
        const strBirth = document.querySelector("input[name='srhStrBirth']").value;
        const endBirth = document.querySelector("input[name='srhEndBirth']").value;
        if (new Date(strBirth) > new Date(endBirth)) {
            alert("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
            return;
        }

        // fetch data
        const data = new URLSearchParams({
            srhName: document.querySelector("input[name='srhName']").value,
            srhStrBirth: strBirth,
            srhEndBirth: endBirth,
            srhAddress: document.querySelector("select[name='srhAddress']").value
        });

        try {
            const res = await fetch(`/testList?${data.toString()}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                },
            });
            return res.json();

        } catch (e) {
            console.error(e);
        }
    }

    // 지역 세팅(공통코드 세팅)
    // getRegionList().then(data => {
    //     console.log(data);
    //     const selectElement = document.querySelector("select[name='address']");
    //
    //     for(const region of data) {
    //         const optionElement = document.createElement("option");
    //         optionElement.value = region.code;  // 코드
    //         optionElement.textContent = region.codeName;  // 이름
    //
    //         selectElement.appendChild(optionElement);
    //     }
    // }).catch(e => {
    //     console.error(e);
    // });


    // 지역 목록 조회 (공통코드 조회)
    // async function getRegionList() {
    //     const mainCode = 'RGN';
    //
    //     const res = await fetch(`/sys/getList${mainCode}`, {
    //         method: 'GET',
    //         headers: {
    //             'Content-Type': 'application/json'
    //         }
    //     });
    //
    //     return res.data;
    // }
}

window.onload = () => {
    init();
}