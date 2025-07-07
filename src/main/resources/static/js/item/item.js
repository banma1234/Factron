// 공통코드를 셀렉트 박스에 세팅
const setSelectBox = async (mainCode, target) => {
    try {
        let data = await getSysCodeList(mainCode);

        if (data.status === 200 && Array.isArray(data.data)) {
            data = data.data.filter((code) => code.detail_code !== "ITP001")

            if (target === 'unit') {
                // 단위 셀렉트 박스 옵션 저장
                window.unitOptions = data.map((code) => ({
                    text: code.name,
                    value: code.detail_code
                }));
            } else if (target === 'itemType') {
                // 옵션 저장 및 화면 적용
                window.itemTypeOptions = data.map((code) => ({
                    text: code.name,
                    value: code.detail_code
                })) || [];

                const itemTypeSelect = document.querySelector('.itemTypeSelect');
                itemTypeSelect.innerHTML = '<option value="">제품 유형</option>'; // 기존 option 초기화

                // 셀렉트 박스에 옵션 추가
                window.itemTypeOptions.forEach(opt => {
                    const option = document.createElement('option');
                    option.value = opt.value;
                    option.text = opt.text;
                    itemTypeSelect.appendChild(option);
                });
            }
        } else {
            throw new Error(`공통코드(${mainCode}) 데이터 형식이 올바르지 않습니다.`);
        }
    } catch (error) {
        console.error(`setSelectBox 에러:`, error);
        throw error;
    }
};

const init = async () => {
    const alertModal = new bootstrap.Modal(document.getElementsByClassName("alertModal")[0]);
    const alertBtn = document.getElementsByClassName("alertBtn")[0];

    // 셀렉트 박스 데이터 세팅
    await setSelectBox("UNT","unit");
    await setSelectBox("ITP", "itemType");

    // 그리드 초기화
    const itemGrid = initGrid(
        document.querySelector('.itemGrid'),
        400,
        [
            { header: '제품코드', name: 'itemId', align: 'center', editable: false },
            { header: '제품명', name: 'name', align: 'center', editor: 'text' },
            { header: '단위', name: 'unit', align: 'center', editor: { type: 'select', options: { listItems: window.unitOptions } }, formatter: 'listItemText' },
            { header: '가격', name: 'price', align: 'center', editor: 'text',  },
            { header: '제품 유형', name: 'typeCode', align: 'center', editor: { type: 'select', options: { listItems: window.itemTypeOptions } }, formatter: 'listItemText' },
            { header: '등록자', name: 'createdBy', align: 'center' },
            { header: '등록일', name: 'createdAt', align: 'center', formatter: ({ value }) => value ? value.substring(0, 10) : '' },
        ]
    );

    // 검색 버튼
    document.querySelector(".searchBtn").addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        refreshGrid();
    }, false);

    // 엔터키 검색
    document.querySelector("input[name='searchName']").addEventListener("keyup", function (e) {
        if (e.key === 'Enter') {
            refreshGrid();
        }
    });

    // 제품유형 변경 시 필터링
    document.querySelector(".itemTypeSelect").addEventListener("change", function () {
        refreshGrid();
    });

    // 항목 추가 버튼
    const addItemBtn = document.querySelector(".addItemBtn");
    if (addItemBtn) {
        addItemBtn.addEventListener("click", function (e) {
            e.preventDefault();
            e.stopPropagation();
            addItem();
        });
    }

// 저장 버튼
    const saveItemBtn = document.querySelector(".saveItemBtn");
    if (saveItemBtn) {
        saveItemBtn.addEventListener("click", function (e) {
            e.preventDefault();
            e.stopPropagation();
            saveItems();
        });
    }

    // 검색 데이터 조회
    async function fetchData() {
        const searchName = document.querySelector("input[name='searchName']").value;
        const itemType = document.querySelector(".itemTypeSelect").value;

        const params = new URLSearchParams({
            itemName: searchName,
            typeCode: itemType
        });

        try {
            const res = await fetch(`/api/item?${params.toString()}`, {
                method: "GET",
                headers: { "Content-Type": "application/json" }
            });
            return res.json();

        } catch (err) {
            console.error("fetch error", err);
            return { data: [] };
        }
    }

    // 더블클릭 시 셀 편집
    itemGrid.on('dblclick', function (ev) {
        if (ev.columnName === 'price') {
            itemGrid.setValue(ev.rowKey, 'price', unformatNumber(itemGrid.getValue(ev.rowKey, 'price')));
        }

        itemGrid.startEditing(ev.rowKey, ev.columnName);
    });

    // 페이지 최초 그리드 데이터 로딩
    fetchData().then(res => {
        res.data.forEach(row => {
            row.price = formatNumber(row.price);
        });
        itemGrid.resetData(res.data);
    });

    // 항목 추가 함수
    const addItem = () => {
        const nextId = generateNextId();
        const newRow = {
            itemId: nextId,
            name: '',
            unit: '',
            price: '',
            typeCode: '',
            createdAt: '',
            createdBy: user.id
        };
        itemGrid.prependRow(newRow, { focus: true });
    };

    // 저장 함수
    const saveItems = () => {
        itemGrid.finishEditing();
        const updatedData = itemGrid.getData();

        const insertData = [];
        const updateData = [];

        // 데이터 분류 (post / put)
        for (let row of updatedData) {
            // 필수 항목 유효성 검사
            if (!row.name || !row.unit || !row.price || !row.typeCode) {
                continue;
            }

            // 가격 0원 이상
            row.price = unformatNumber(row.price);
            if (isNaN(row.price) || Number(row.price) < 0) {
                alert('가격은 0원 이상이어야 합니다.');
                return;
            }

            if (row.createdAt) {
                updateData.push(row); // 수정
            } else {
                insertData.push(row); // 저장
            }
        }

        if (insertData.length === 0 && updateData.length === 0) {
            alert('입력된 데이터가 없습니다.');
            return;
        }

        // 동시 저장
        (async () => {
            try {
                // 신규 저장
                if (insertData.length > 0) {
                    await fetch('/api/item', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(insertData)
                    });
                }

                // 수정 저장
                if (updateData.length > 0) {
                    await fetch('/api/item', {
                        method: 'PUT',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(updateData)
                    });
                }

                alertModal.show();

            } catch (err) {
                console.error('저장 실패', err);
            }
        })();
    };

    // alert 모달 확인 버튼
    alertBtn.addEventListener("click", () => {
        alertModal.hide();
        refreshGrid();
    });

    // ID 자동 생성
    const generateNextId = () => {
        const data = itemGrid.getData();
        let maxId = 0;

        data.forEach(row => {
            if (row.itemId && row.itemId.startsWith('P')) {
                const numericPart = parseInt(row.itemId.substring(1), 10);
                if (!isNaN(numericPart) && numericPart > maxId) {
                    maxId = numericPart;
                }
            }
        });

        const nextIdNumber = maxId + 1;
        return 'P' + nextIdNumber.toString().padStart(7, '0');
    };

    // 그리드 새로고침
    const refreshGrid = async () => {
        const res = await fetchData();
        res.data.forEach(row => {
            row.price = formatNumber(row.price);
        });
        itemGrid.resetData(res.data);
    };

};

window.onload = () => {
    init();
};
