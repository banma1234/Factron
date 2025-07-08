// 공통코드를 셀렉트 박스에 세팅
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
    const alertModal = new bootstrap.Modal(document.getElementsByClassName("alertModal")[0]);
    const alertBtn = document.getElementsByClassName("alertBtn")[0];

    // 셀렉트 박스 데이터 세팅
    await setSelectBox("UNT");

    // 그리드 초기화
    const materialGrid = initGrid(
        document.getElementById('materialGrid'),
        400,
        [
            { header: '자재코드', name: 'materialId', align: 'center', editable: false },
            { header: '자재명', name: 'name', align: 'center', editor: 'text' },
            { header: '자재 정보', name: 'info', align: 'center', editor: 'text' },
            { header: '단위', name: 'unit', align: 'center', editor: { type: 'select', options: { listItems: window.unitOptions } },formatter: 'listItemText'  },
            { header: '자재 사양', name: 'spec', align: 'center', editor: 'text' },
            { header: '등록자', name: 'createdBy', align: 'center' },
            { header: '등록일', name: 'createdAt', align: 'center', formatter: ({ value }) => value ? value.substring(0, 10) : '' },
        ]
    );

    // 검색 버튼
    document.querySelector(".searchBtn").addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        fetchData().then(res => {
            materialGrid.resetData(res.data);
        });
    }, false);

    // 엔터키 검색
    document.querySelector("input[name='searchName']").addEventListener("keyup", function (e) {
        if (e.key === 'Enter') {
            fetchData().then(res => {
                materialGrid.resetData(res.data);
            });
        }
    });

    // material 추가 버튼
    document.querySelector(".addMaterialBtn").addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        addMaterial();
    });

    // material 저장 버튼
    document.querySelector(".saveMaterialBtn").addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        saveMaterials();
    });

    // material 데이터 조회 함수
    async function fetchData() {
        const searchName = document.querySelector("input[name='searchName']").value;

        try {
            const res = await fetch(`/api/material?materialName=${searchName}`, {
                method: "GET",
                headers: { "Content-Type": "application/json" }
            });
            return res.json();

        } catch (err) {
            console.error("fetch error", err);
            return { data: [] };
        }
    }

    // 그리드 더블 클릭 시 편집
    materialGrid.on('dblclick', function (ev) {
        materialGrid.startEditing(ev.rowKey, ev.columnName);
    });

    // 페이지 최초 그리드 데이터 로딩
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
    const saveMaterials = async () => {
        const allData = materialGrid.getModifiedRows();
        const newRows = allData.createdRows;
        const editedRows = allData.updatedRows;

        console.log('allData', allData);
        console.log('newRows', newRows);
        console.log('editedRows', editedRows);

        // Validation 체크
        const validationErrors = [];

        newRows.forEach((row, index) => {
            if (!row.name || row.name.trim() === '') {
                validationErrors.push(`새로 추가된 행 ${index + 1}: 자재명은 필수 입력 항목입니다.`);
            }
            if (!row.info || row.info.trim() === '') {
                validationErrors.push(`새로 추가된 행 ${index + 1}: 자재 정보는 필수 입력 항목입니다.`);
            }
            if (!row.unit || row.unit.trim() === '') {
                validationErrors.push(`새로 추가된 행 ${index + 1}: 단위는 필수 입력 항목입니다.`);
            }
            if (!row.spec || row.spec.trim() === '') {
                validationErrors.push(`새로 추가된 행 ${index + 1}: 자재 사양은 필수 입력 항목입니다.`);
            }
        });

        editedRows.forEach((row, index) => {
            if (!row.name || row.name.trim() === '') {
                validationErrors.push(`수정된 행 ${index + 1}: 자재명은 필수 입력 항목입니다.`);
            }
            if (!row.info || row.info.trim() === '') {
                validationErrors.push(`수정된 행 ${index + 1}: 자재 정보는 필수 입력 항목입니다.`);
            }
            if (!row.unit || row.unit.trim() === '') {
                validationErrors.push(`수정된 행 ${index + 1}: 단위는 필수 입력 항목입니다.`);
            }
            if (!row.spec || row.spec.trim() === '') {
                validationErrors.push(`수정된 행 ${index + 1}: 자재 사양은 필수 입력 항목입니다.`);
            }
        });

        // 벨리데이션 에러가 있으면 알림 후 저장 중단
        if (validationErrors.length > 0) {
            alert('다음 항목들을 확인해주세요:\n\n' + validationErrors.join('\n'));
            return;
        }

        // 두 API를 병렬로 호출
        const promises = [];

        if (newRows.length > 0) {
            promises.push(saveNewMaterials(newRows));
        }

        if (editedRows.length > 0) {
            promises.push(updateMaterials(editedRows));
        }

        if(promises.length === 0) {
            alert('데이터를 추가하거나 수정해주세요!');
            return;
        }

        // 병렬로 저장 처리
        try {
            const results = await Promise.all(promises);
            const res = await fetchData();
            materialGrid.resetData(res.data);
            alertModal.show();
        } catch (error) {
            console.error('저장 중 오류:', error);
            alert('저장 중 오류가 발생했습니다: ' + error.message);
        }
    };

    // 신규 자재 저장 함수
    const saveNewMaterials = async (newRows) => {
        try {
            const response = await fetch('/api/material', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(newRows)
            });
            
            if (!response.ok) {
                throw new Error(`신규 저장 실패: ${response.status}`);
            }
            
            return await response.json();
        } catch (error) {
            console.error('신규 저장 에러:', error);
            throw error;
        }
    };

    // 자재 수정 함수
    const updateMaterials = async (editedRows) => {
        try {
            const response = await fetch('/api/material', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(editedRows)
            });
            
            if (!response.ok) {
                throw new Error(`수정 저장 실패: ${response.status}`);
            }
            
            return await response.json();
        } catch (error) {
            console.error('수정 저장 에러:', error);
            throw error;
        }
    };

    // alert 모달 확인 버튼
    alertBtn.addEventListener("click", () => {
        alertModal.hide();
        fetchData().then(res => {
            materialGrid.resetData(res.data);
        });
    });

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
