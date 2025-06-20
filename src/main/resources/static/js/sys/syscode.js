/**
 * window 로드 후 실행할 스크립트
 * @return void
 * */
const sysInit = () => {
    /**
     * `sysCode` 테이블 초기화
     * */
    const mainGrid = initGrid(
        document.getElementById('grid_main'),
        400,
        [
            {
                header: '구분코드',
                name: 'main_code',
                align: 'center'
            },
            {
                header: '구분명',
                name: 'name',
                align: 'center'
            },
            {
                header: '사용여부',
                name: 'is_active',
                align: 'center',
            },
        ]
    )

    /**
     * `detailSysCode` 테이블 초기화
     * */
    const detailGrid = initGrid(
        document.getElementById('grid_detail'),
        400,
        [
            {
                header: '구분코드',
                name: 'detail_code',
                align: 'center'
            },
            {
                header: '구분명',
                name: 'name',
                align: 'center'
            },
            {
                header: '사용여부',
                name: 'is_active',
                align: 'center',
            },
        ]
    )

    /**
     * 클릭한 `컬럼`의 데이터 저장하는 `전역변수`
     * */
    let selectedRowData = null;

    /**
     * `sysCode` 요청 api
     * @param mainCode 메인코드
     * @return JSON
     * */
    const getMainCode = async (mainCode) => {
        try {
            const res = await fetch(`/api/sys/main?mainCode=${mainCode}`, {
                method: 'GET',
                headers: {
                    "Content-Type": "application/json"
                }
            });

            return res.json();
        } catch (e) {
            console.error(e);
        }
    }

    /**
     * `detailSysCode` 요청 api
     * @param mainCode 메인코드
     * @param name 구분명
     * @return JSON
     * */
    const getDetailCode = async (mainCode, name) => {
        try {
            const res = await fetch(`/api/sys/detail?mainCode=${mainCode}&name=${name}`, {
                method: 'GET',
                headers: {
                    "Content-Type": "application/json"
                }
            });

            return res.json();
        } catch (e) {
            console.error(e);
        }
    }

    /**
     * `detailSysCode` 그리드 이벤트 연결
     * */
    const setDetailGridEvent = () => {
        detailGrid.on('click', e => {
            const rowKey = e.rowKey;
            selectedRowData = detailGrid.getRow(rowKey);
        })
    }

    /**
     * `sysCode` 그리드 이벤트 연결
     * @return void
     * */
    const setMainGridEvent = () => {
        mainGrid.on('click', e => {
            const rowKey = e.rowKey;
            selectedRowData = mainGrid.getRow(rowKey);

            getDetailCode(selectedRowData.main_code, "").then(res => {
                detailGrid.resetData(res.data);

                setDetailGridEvent();
            })
        });
    }

    getMainCode("").then(res => {
        mainGrid.resetData(res.data);

        setMainGridEvent();
    })


    /**
     * `삽입` / `수정` 완료시 그리드 데이터 초기화
     * @return void
     * */
    const refreshDataOnPopup = async () => {
        await getMainCode("").then(res => {
            mainGrid.resetData(res.data);
        })

        if (selectedRowData) {
            await getDetailCode(selectedRowData.main_code, "").then(res => {
                detailGrid.resetData(res.data);
            })
        }
    };

    /**
     * 팝업창 오픈 - `수정모드`
     * @param isEditMode 수정모드 여부
     * @return void
     * */
    const openUpdatePopup = (type, isEditMode) => {
        const rowData = selectedRowData || undefined;
        let url = `/sys/sys-form?target=${type}`;

        if ((isEditMode && !rowData) || (type === "detail" && !rowData.detail_code && isEditMode)) {
            console.log(type);
            alert("대상을 지정해주세요");
            return;
        }

        const popup = window.open(
            url,
            '_blank',
            'width=800,height=400'
        );

        if (!popup) {
            alert("팝업이 차단되었습니다. 팝업 차단을 해제해주세요.");
            return;
        }

        /**
         * 팝업창에 데이터 전달
         * @return void
         * */
        const messageHandler = (event) => {
            if (event.data === 'ready') {

                popup.postMessage({
                    main_code: rowData.main_code ? rowData.main_code : "",
                    detail_code: rowData.detail_code && isEditMode ? rowData.detail_code : "",
                    name: rowData.name,
                    is_active: rowData.is_active,
                    is_edit_mode: isEditMode,
                }, "*");

                window.removeEventListener("message", messageHandler);
            }
        };

        window.addEventListener("message", messageHandler);
    }

    /**
     * `sysCode` 삽입 버튼
     * */
    document.querySelector("button[name='postSysMainBtn']")
        .addEventListener('click', e => {
            e.preventDefault();
            e.stopPropagation();

            openUpdatePopup("main", false);
        });

    /**
     * `detailSysCode` 삽입 버튼
     * */
    document.querySelector("button[name='postSysDetailBtn']")
        .addEventListener('click', e => {
            e.preventDefault();
            e.stopPropagation();

            openUpdatePopup("detail", false);
        });

    /**
     * `sysCode` 수정 버튼
     * */
    document.querySelectorAll(".updateSysMainBtn")
        .forEach(btn => {
            btn.addEventListener('click', e => {
                e.preventDefault();
                e.stopPropagation();

                openUpdatePopup("main", true);
            });
        });

    /**
     * `detailSysCode` 수정 버튼
     * */
    document.querySelectorAll(".updateSysDetailBtn")
        .forEach(btn => {
            btn.addEventListener('click', e => {
                e.preventDefault();
                e.stopPropagation();

                openUpdatePopup("detail", true);
            });
        });

    /**
     * `sysCode` 검색(AJAX) 인풋
     * */
    document.querySelector("input[name='srhMain']")
        .addEventListener('input', e => {
            e.preventDefault();
            const keyword = e.target.value;

            getMainCode(keyword).then(res => {
                mainGrid.resetData(res.data);
                detailGrid.resetData([]);
            })
        });

    /**
     * `detailSysCode` 검색(AJAX) 인풋
     * */
    document.querySelector("input[name='srhDetail']")
        .addEventListener('input', e => {
            e.preventDefault();
            const keyword = e.target.value;

            getDetailCode(selectedRowData.main_code, keyword).then(res => {
                detailGrid.resetData(res.data);
            })
        });

    window["sysInit"] = sysInit;
    window["refreshDataOnPopup"] = refreshDataOnPopup;

};

window.onload = () => {
    sysInit();
};