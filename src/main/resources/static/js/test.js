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
                formatter: (value) => {
                    if (value.value) {
                        const imageUrl = value.value;
                        console.log("이미지경로:"+imageUrl)
                        const absoluteImageUrl = `/test/uploads/${imageUrl}`;
                        return `<img src="${absoluteImageUrl}" alt="이미지" style="max-width: 100px; max-height: 25px;">`;
                    }
                    return "";
                }
            },
            {
                header: '등록일',
                name: 'regDate',
                // formatter: (value) => {
                //     if (value) {
                //         const data = value.value;
                //         return `${data[0]}-${data[1]}-${data[2]}`;
                //     }
                //     return "";
                // }
            }
        ],
        // data: [
        //     {
        //         id: 1,
        //         name: '계두식',
        //         chkType: false,
        //         birth: new Date(),
        //         address: '부산광역시 XX구 XX동',
        //         filePath: ' ',
        //         regDate: new Date(),
        //     },
        //     {+
        //         id: 2,
        //         name: '강철중',
        //         chkType: true,
        //         birth: new Date(),
        //         address: '서울특별시 XX구 XX동',
        //         filePath: ' ',
        //         regDate: new Date(),
        //     },
        //     {
        //         id: 3,
        //         name: '김갑환',
        //         chkType: false,
        //         birth: new Date(),
        //         address: '대구광역시 XX구 XX동',
        //         filePath: ' ',
        //         regDate: new Date(),
        //     }
        // ]
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
        getData();
    }, false);

    // 목록 조회
    async function getData() {
        // validation
        const strBirth = document.querySelector("input[name='srhStrBirth']").value;
        const endBirth = document.querySelector("input[name='srhEndBirth']").value;
        if (new Date(strBirth) > new Date(endBirth)) {
            alert("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
            return;
        }

        // fetch data
        // parameter로 보내는 경우
        const data = new URLSearchParams({
            srhName: document.querySelector("input[name='srhName']").value,
            srhStrBirth: strBirth,
            srhEndBirth: endBirth,
            srhAddress: document.querySelector("select[name='srhAddress']").value
        });

        // body로 보내는 경우
        // const data = {
        //     srhName: document.querySelector("input[name='srhName']").value,
        //     srhStrBirth: strBirth,
        //     srhEndBirth: endBirth,
        //     srhAddress: document.querySelector("select[name='srhAddress']").value
        // };

        try {
            const res = await fetch(`/testList?${data.toString()}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                },
                // body: JSON.stringify(data),
            });
            res.json().then(res => {
                testGrid.resetData(res.data); // grid에 세팅
            });
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