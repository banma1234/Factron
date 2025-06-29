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
const setSelectBox = async (mainCode) => {
    try {
        const data = await getSysCodeList(mainCode);

        if (data.status === 200 && Array.isArray(data.data)) {
            window.unitOptions = data.data.map((code) => ({
                text: code.name,
                value: code.detail_code
            }));
        } else {
            throw new Error(`공통코드(${mainCode}) 데이터 형식이 올바르지 않습니다.`);
        }
    } catch (error) {
        console.error(`setSelectBox 에러:`, error);
        throw error;
    }
};

const init = async () => {

    await setSelectBox("UNT");

    const materialGrid = initGrid(
        document.getElementById('materialGrid'),
        400,
        [
            { header: 'ID', name: 'materialId', align: 'center', editable: false },
            { header: '자재명', name: 'name', align: 'center', editor: 'text' },
            { header: '자재 정보', name: 'info', align: 'center', editor: 'text' },
            { header: '단위', name: 'unit', align: 'center', editor: { type: 'select', options: { listItems: window.unitOptions } },formatter: 'listItemText'  },
            { header: '자재사양', name: 'spec', align: 'center', editor: 'text' },
            { header: '등록자', name: 'createdBy', align: 'center' },
            { header: '등록일', name: 'createdAt', align: 'center', formatter: ({ value }) => value ? value.substring(0, 10) : '' },
        ]
    );

    // 검색 버튼 클릭
    document.querySelector("#searchBtn").addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        fetchData().then(res => {
            materialGrid.resetData(res.data);
        });
    }, false);

    // 엔터 시 검색
    document.querySelector(".search__form").addEventListener("submit", function (e) {
        e.preventDefault();

        fetchData().then(res => {
            materialGrid.resetData(res.data);
        });
    });

    // material 데이터 조회 함수
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
            materialName: searchName,
            materialStartDate: startDate,
            materialEndDate: endDate
        });

        try {
            const res = await fetch(`/api/material?${params.toString()}`, {
                method: "GET",
                headers: { "Content-Type": "application/json" }
            });
            return res.json();

        } catch (err) {
            console.error("fetch error", err);
            return { data: [] };
        }
    }

    // material 추가 버튼
    document.querySelector("#addMaterialBtn").addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        addMaterial();
    });

    // material 저장 버튼
    document.querySelector(".header-row button:nth-child(3)").addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        saveMaterials();
    });

    // 그리드 더블 클릭 시 편집
    materialGrid.on('dblclick', function (ev) {
        materialGrid.startEditing(ev.rowKey, ev.columnName);
    });

    // 초기 데이터 조회
    fetchData().then(res => {
        materialGrid.resetData(res.data);
    });

    // material 추가 함수
    const addMaterial = () => {
        const nextId = generateNextId();
        const newRow = {
            materialId: nextId,
            name: '',
            unit: '',
            info: '',
            spec: '',
            createdAt: '',
            createdBy: user.id
        };
        materialGrid.prependRow(newRow, { focus: true });
    };

    // material 저장 함수
    const saveMaterials = () => {
        materialGrid.finishEditing();
        const updatedData = materialGrid.getData();

        (async () => {
            try {
                for (let row of updatedData) {
                    const method = row.createdAt ? 'PUT' : 'POST';

                    const payload = {
                        materialId: row.materialId,
                        name: row.name,
                        unit: row.unit,
                        info: row.info,
                        spec: row.spec,
                        createdBy: !row.createdAt ? user.id : null,
                        updatedBy: row.createdAt ? user.id : null
                    };

                    await fetch('/api/material', {
                        method: method,
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(payload)
                    });
                }
                alert('저장 성공');
                fetchData().then(res => {
                    materialGrid.resetData(res.data);
                });
            } catch (err) {
                console.error('저장 실패', err);
            }
        })();
    };

    // materialID 생성 함수
    const generateNextId = () => {
        const data = materialGrid.getData();
        let maxId = 0;

        data.forEach(row => {
            if (row.materialId && row.materialId.startsWith('M')) {
                const numericPart = parseInt(row.materialId.substring(1), 10);
                if (!isNaN(numericPart) && numericPart > maxId) {
                    maxId = numericPart;
                }
            }
        });

        const nextIdNumber = maxId + 1;
        return 'M' + nextIdNumber.toString().padStart(7, '0');
    };
};

window.onload = () => {
    init();
};
