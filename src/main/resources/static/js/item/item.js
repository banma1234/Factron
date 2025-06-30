const getSysCodeList = async (mainCode) => {
    try {
        const res = await fetch(`/api/sys/detail?mainCode=${mainCode}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        if (!res.ok) {
            throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.json();
    } catch (error) {
        console.error(`getSysCodeList 에러 (${mainCode}):`, error);
        throw error;
    }
};

const setSelectBox = async (mainCode, target) => {
    try {
        const data = await getSysCodeList(mainCode);

        if (data.status === 200 && Array.isArray(data.data)) {
            if (target === 'unit') {
                window.unitOptions = data.data.map((code) => ({
                    text: code.name,
                    value: code.detail_code
                }));
            } else if (target === 'itemType') {
                window.itemTypeOptions = data.data.map((code) => ({
                    text: code.name,
                    value: code.detail_code
                })) || [];
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

    await setSelectBox("UNT","unit");
    await setSelectBox("ITP", "itemType");

    const itemGrid = initGrid(
        document.getElementById('itemGrid'),
        400,
        [
            { header: 'ID', name: 'itemId', align: 'center', editable: false },
            { header: '제품명', name: 'name', align: 'center', editor: 'text' },
            { header: '단위', name: 'unit', align: 'center', editor: { type: 'select', options: { listItems: window.unitOptions } }, formatter: 'listItemText' },
            { header: '가격', name: 'price', align: 'center', editor: 'text' },
            { header: '제품 유형', name: 'typeCode', align: 'center', editor: { type: 'select', options: { listItems: window.itemTypeOptions } }, formatter: 'listItemText' },
            { header: '등록자', name: 'createdBy', align: 'center' },
            { header: '등록일', name: 'createdAt', align: 'center', formatter: ({ value }) => value ? value.substring(0, 10) : '' },
        ]
    );

    document.querySelector("#searchBtn").addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        fetchData().then(res => {
            itemGrid.resetData(res.data);
        });
    }, false);

    document.querySelector(".search__form").addEventListener("submit", function (e) {
        e.preventDefault();

        fetchData().then(res => {
            itemGrid.resetData(res.data);
        });
    });

    async function fetchData() {
        const searchName = document.querySelector("input[name='searchName']").value;
        const startDate = document.querySelector("input[name='startDate']").value;
        const endDate = document.querySelector("input[name='endDate']").value;


        // 날짜 유효성 검사
        if ((startDate && !endDate) || (!startDate && endDate)) {
            alert("시작 및 종료 날짜를 모두 입력해주세요.");
            return { data: [] };
        }

        if (startDate && endDate) {
            const dateRegex = /^\d{4}-\d{2}-\d{2}$/;

            if (!dateRegex.test(startDate) || isNaN(Date.parse(startDate))
                || !dateRegex.test(endDate) || isNaN(Date.parse(endDate))) {
                alert("날짜 형식이 올바르지 않습니다.");
                return { data: [] };
            }

            if (new Date(startDate) > new Date(endDate)) {
                alert("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
                return { data: [] };
            }
        }

        const params = new URLSearchParams({
            itemName: searchName,
            itemStartDate: startDate,
            itemEndDate: endDate
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

    document.querySelector("#addItemBtn").addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        addItem();
    });

    document.querySelector(".header-row button:nth-child(3)").addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        saveItems();
    });

    itemGrid.on('dblclick', function (ev) {
        itemGrid.startEditing(ev.rowKey, ev.columnName);
    });

    fetchData().then(res => {
        itemGrid.resetData(res.data);
    });

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

    const saveItems = () => {
        itemGrid.finishEditing();
        const updatedData = itemGrid.getData();

        (async () => {
            try {
                for (let row of updatedData) {
                    const method = row.createdAt ? 'PUT' : 'POST';

                    const payload = {
                        itemId: row.itemId,
                        name: row.name,
                        unit: row.unit,
                        price: row.price,
                        typeCode: row.typeCode,
                        createdBy: !row.createdAt ? user.id : null,
                        updatedBy: row.createdAt ? user.id : null
                    };

                    await fetch('/api/item', {
                        method: method,
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(payload)
                    });
                }
                alert('저장 성공');
                fetchData().then(res => {
                    itemGrid.resetData(res.data);
                });
            } catch (err) {
                console.error('저장 실패', err);
            }
        })();
    };

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
};

window.onload = () => {
    init();
};
