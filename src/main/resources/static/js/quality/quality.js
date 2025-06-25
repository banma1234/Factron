// 공백 제거 함수
const removeSpaces = (str) => {
    return str.replace(/\s+/g, '');
}

const initSelectGrid = (gridEl, bodyHeight, columns, option = []) => {
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
        // header: {
        //     complexColumns:[
        //         {
        //             header: '',
        //             name: 'itemName',
        //             childNames: ['id','qualityInspectionId','itemId','targetValue','upperLimit','lowerLimit','unit',],
        //         },
        //     ],
        //     height: 60,
        // },
        scrollY: true,
        rowHeaders: option,
        bodyHeight: bodyHeight,
        columns: columns,
    });
}

const init = () => {
    const inspGrid = initGrid(
        document.getElementById('grid_insp'),
        400,
        [
            {
                header: '품질검사번호',
                name: 'inspectionId',
                align: 'center'
            },
            {
                header: '품질검사명',
                name: 'inspectionName',
                align: 'center',
                editor: 'text'
            },
            {
                header: '검사타입',
                name: 'inspectionType',
                align: 'center',
                editor: 'text'
            },
            {
                header: '검사방법',
                name: 'inspectionMethod',
                align: 'center',
                editor: 'text'
            }
        ]
    );

    const stdGrid = initSelectGrid(
        document.getElementById('grid_std'),
        400,
        [
            {
                header: '검사기준번호',
                name: 'qualityInspectionStandardId',
                align: 'center'
            },
            {
                header: '품질검사번호',
                name: 'qualityInspectionId',
                align: 'center',
                editor: 'text'
            },
            {
                header: '제품번호',
                name: 'itemId',
                align: 'center',
                editor: 'text'
            },
            {
                header: '목표값',
                name: 'targetValue',
                align: 'center',
                editor: 'text'
            },
            {
                header: '상한값',
                name: 'upperLimit',
                align: 'center',
                editor: 'text'
            },
            {
                header: '하한값',
                name: 'lowerLimit',
                align: 'center',
                editor: 'text'
            },
            {
                header: '단위',
                name: 'unit',
                align: 'center',
                editor: 'text'
            }
        ],['checkbox']
    );

    async function getInspections() {
        const name = removeSpaces(document.querySelector("input[name='inspName']").value);

        // params 생성
        const params = new URLSearchParams();

        // params에 검색어 추가
        if(name) params.append("qualityInspectionNameOrId", name)

        // 사원 목록 API 호출
        fetch(`/api/quality?${params.toString()}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).then(res => res.json())
            .then(res => {
                if(res.status === 200){
                    return inspGrid.resetData(res.data); // grid에 세팅
                }else{
                    alert(res.message);
                }
                return inspGrid.resetData([]);
            })
            .catch(e => {
                alert(e);
            });
    }

    async function getQualityStandards() {
        const itemName = removeSpaces(document.querySelector("input[name='itemName']").value);

        // params 생성
        const params = new URLSearchParams();

        // params에 검색어 추가
        if(itemName) params.append("qualityInspectionStandardNameOrId", itemName)

        // 품질검사 기준 API 호출
        fetch(`/api/quality/standard?${params.toString()}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).then(res => res.json())
            .then(res => {
                if(res.status === 200){
                    stdGrid.setColumnHeaders({itemName: "test"})
                    return stdGrid.resetData(res.data); // grid에 세팅
                }else{
                    alert(res.message);
                }
                return stdGrid.resetData([]);
            })
            .catch(e => {
                alert(e);
            });
    }

    async function getQualityStandardsById(id) {
        // 품질검사 기준 API 호출
        fetch(`/api/quality/standard/${id}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).then(res => res.json())
            .then(res => {
                if(res.status === 200){
                    return stdGrid.resetData(res.data); // grid에 세팅
                }else{
                    alert(res.message);
                }
                return stdGrid.resetData([]);
            })
            .catch(e => {
                alert(e);
            });
    }

    getInspections();
    getQualityStandards();

    // 품질검사 검색 버튼 이벤트
    document.querySelector('button[name="inspSrhBtn"]').addEventListener('click', function(e) {
        e.preventDefault();
        getInspections();
    });

    // 품질검사 기준 검색 버튼 이벤트
    document.querySelector('button[name="stdSrhBtn"]').addEventListener('click', function(e) {
        e.preventDefault();
        getQualityStandards();
    });

    // 품질검사 추가 버튼 이벤트
    document.querySelector('button[name="addInspRow"]').addEventListener('click', function(e) {
        e.preventDefault();
        addNewInspRow();
    });

    // 품질검사 수정 버튼 이벤트
    document.querySelector('button[name="updateInsp"]').addEventListener('click', function(e) {
        e.preventDefault();
        saveInspData();
    });

    // 품질검사 기준 추가 버튼 이벤트
    document.querySelector('button[name="addInspStdRow"]').addEventListener('click', function(e) {
        e.preventDefault();
        addNewStdRow();
    });

    // 품질검사 기준 수정 버튼 이벤트
    document.querySelector('button[name="updateInspStd"]').addEventListener('click', function(e) {
        e.preventDefault();
        saveStdData();
    });

    // 품질검사 기준 삭제 버튼 이벤트
    document.querySelector('button[name="deleteInspStd"]').addEventListener('click', function(e) {
        e.preventDefault();
        deleteStdData();
    });

    // 품질검사 새 행 추가 함수
    function addNewInspRow() {
        const newRow = {
            id: '', // 새로 생성될 ID
            inspectionName: '',
            inspectionType: '',
            inspectionMethod: ''
        };
        
        inspGrid.appendRow(newRow);
    }

    // 품질검사 기준 새 행 추가 함수
    function addNewStdRow() {
        const newRow = {
            id: '', // 새로 생성될 ID
            qualityInspectionId: '',
            itemId: '',
            targetValue: '',
            upperLimit: '',
            lowerLimit: '',
            unit: ''
        };
        
        stdGrid.appendRow(newRow);
    }

    // 그리드 더블클릭시 오른쪽에 해당 품질검사 추가
    inspGrid.on('dblclick', (ev) => {
        const id = inspGrid.getRow(ev.rowKey).inspectionId;
        if(id){
            getQualityStandardsById(id);
        }
    });

    // 나중에 변경 validation 추가하기!
    inspGrid.on('afterChange', (ev) => {
    });

    // 품질검사 저장 함수
    async function saveInspData() {
        const allData = inspGrid.getModifiedRows();
        const newRows = allData.createdRows;
        const editedRows = allData.updatedRows;

        // 두 API를 병렬로 호출
        const promises = [];

        if (newRows.length > 0) {
            promises.push(saveNewInspections(newRows));
        }
        
        if (editedRows.length > 0) {
            promises.push(updateInspections(editedRows));
        }

        if(promises.length === 0) {
            alert('데이터를 추가하거나 수정해주세요!');
            return;
        }
        
        try {
            const results = await Promise.all(promises);
            alert('모든 데이터가 성공적으로 저장되었습니다.');
            getInspections(); // 그리드 새로고침
        } catch (error) {
            console.error('저장 중 오류:', error);
            alert('저장 중 오류가 발생했습니다.');
        }
    }

    // 새 품질검사 저장 함수
    async function saveNewInspections(newRows) {
        // Validation 체크
        const validationErrors = [];
        newRows.forEach((row, index) => {
            if (!row.inspectionName || row.inspectionName.trim() === '') {
                validationErrors.push(`${index + 1}번째 행: 품질검사명은 필수입니다.`);
            }
            if (!row.inspectionType || row.inspectionType.trim() === '') {
                validationErrors.push(`${index + 1}번째 행: 검사타입은 필수입니다.`);
            }
            if (!row.inspectionMethod || row.inspectionMethod.trim() === '') {
                validationErrors.push(`${index + 1}번째 행: 검사방법은 필수입니다.`);
            }
        });

        if (validationErrors.length > 0) {
            throw new Error('다음 오류를 수정해주세요:\n' + validationErrors.join('\n'));
        }

        // API 호출을 위한 데이터 변환
        const requestData = {
            newInspection: newRows.map(row => ({
                inspectionId: row.inspectionId || "",
                inspectionName: row.inspectionName.trim(),
                inspectionType: row.inspectionType.trim(),
                inspectionMethod: row.inspectionMethod.trim()
            }))
        };

        const response = await fetch('/api/quality', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestData)
        });

        const result = await response.json();

        if (result.status !== 200) {
            throw new Error('저장 중 오류가 발생했습니다: ' + result.message);
        }

        return result;
    }

    async function updateInspections(editedRows) {
        // Validation 체크
        const validationErrors = [];
        editedRows.forEach((row, index) => {
            if (!row.inspectionId || row.inspectionId.trim() === '') {
                validationErrors.push(`${index + 1}번째 행: 품질검사번호는 필수입니다.`);
            }
            if (!row.inspectionName || row.inspectionName.trim() === '') {
                validationErrors.push(`${index + 1}번째 행: 품질검사명은 필수입니다.`);
            }
            if (!row.inspectionType || row.inspectionType.trim() === '') {
                validationErrors.push(`${index + 1}번째 행: 검사타입은 필수입니다.`);
            }
            if (!row.inspectionMethod || row.inspectionMethod.trim() === '') {
                validationErrors.push(`${index + 1}번째 행: 검사방법은 필수입니다.`);
            }
        });

        if (validationErrors.length > 0) {
            throw new Error('다음 오류를 수정해주세요:\n' + validationErrors.join('\n'));
        }

        const requestData = {
            updateInspectionList: editedRows.map(row => ({
                inspectionId: row.inspectionId,
                inspectionName: row.inspectionName.trim(),
                inspectionType: row.inspectionType.trim(),
                inspectionMethod: row.inspectionMethod.trim()
            }))
        };

        const response = await fetch('/api/quality', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestData)
        });

        const result = await response.json();

        if (result.status !== 200) {
            throw new Error('수정 중 오류가 발생했습니다: ' + result.message);
        }

        return result;
    }

    // 품질검사 기준 저장 함수
    async function saveStdData() {
        const allData = stdGrid.getModifiedRows();
        const newRows = allData.createdRows;
        const editedRows = allData.updatedRows;


        // 두 API를 병렬로 호출
        const promises = [];

        if (newRows.length > 0) {
            promises.push(saveNewStdections(newRows));
        }

        if (editedRows.length > 0) {
            promises.push(updateStdections(editedRows));
        }

        if(promises.length === 0) {
            alert('데이터를 추가하거나 수정해주세요!');
            return;
        }

        try {
            const results = await Promise.all(promises);
            getQualityStandards();
            alert('모든 데이터가 성공적으로 저장되었습니다.');
        } catch (error) {
            console.error('저장 중 오류:', error);
            alert(error.message);
        }
    }

    async function saveNewStdections(newRows) {
        // Validation 체크
        const validationErrors = [];
        newRows.forEach((row, index) => {
            if (!row.qualityInspectionId || row.qualityInspectionId.toString().trim() === '') {
                validationErrors.push(`${index + 1}번째 행: 품질검사번호는 필수입니다.`);
            }
            if (!row.itemId || row.itemId.toString().trim() === '') {
                validationErrors.push(`${index + 1}번째 행: 제품번호는 필수입니다.`);
            }
            if (!row.targetValue || row.targetValue.toString().trim() === '') {
                validationErrors.push(`${index + 1}번째 행: 목표값은 필수입니다.`);
            }
            if (!row.upperLimit || row.upperLimit.toString().trim() === '') {
                validationErrors.push(`${index + 1}번째 행: 상한값은 필수입니다.`);
            }
            if (!row.lowerLimit || row.lowerLimit.toString().trim() === '') {
                validationErrors.push(`${index + 1}번째 행: 하한값은 필수입니다.`);
            }
            if (!row.unit || row.unit.trim() === '') {
                validationErrors.push(`${index + 1}번째 행: 단위는 필수입니다.`);
            }
        });

        if (validationErrors.length > 0) {
            throw new Error('다음 오류를 수정해주세요:\n' + validationErrors.join('\n'));
        }

        // API 호출을 위한 데이터 변환
        const requestData = {
            qualityInspectionStandard: newRows.map(row => ({
                qualityInspectionId: parseInt(row.qualityInspectionId),
                targetValue: row.targetValue.toString().trim(),
                upperLimit: parseFloat(row.upperLimit),
                lowerLimit: parseFloat(row.lowerLimit),
                unit: row.unit.trim(),
                itemId: row.itemId.toString().trim()
            }))
        };

        const response = await fetch('/api/quality/standard', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestData)
        });

        const result = await response.json();

        if (result.status !== 200) {
            throw new Error('저장 중 오류가 발생했습니다: ' + result.message);
        }

        return result;
    }

    async function updateStdections(editedRows) {
        // Validation 체크
        const validationErrors = [];
        editedRows.forEach((row, index) => {
            if (!row.qualityInspectionStandardId) {
                validationErrors.push(`${index + 1}번째 행: 검사기준번호는 필수입니다.`);
            }
            if (!row.qualityInspectionId || row.qualityInspectionId.toString().trim() === '') {
                validationErrors.push(`${index + 1}번째 행: 품질검사번호는 필수입니다.`);
            }
            if (!row.itemId || row.itemId.toString().trim() === '') {
                validationErrors.push(`${index + 1}번째 행: 제품번호는 필수입니다.`);
            }
            if (!row.targetValue || row.targetValue.toString().trim() === '') {
                validationErrors.push(`${index + 1}번째 행: 목표값은 필수입니다.`);
            }
            if (!row.upperLimit || row.upperLimit.toString().trim() === '') {
                validationErrors.push(`${index + 1}번째 행: 상한값은 필수입니다.`);
            }
            if (!row.lowerLimit || row.lowerLimit.toString().trim() === '') {
                validationErrors.push(`${index + 1}번째 행: 하한값은 필수입니다.`);
            }
            if (!row.unit || row.unit.trim() === '') {
                validationErrors.push(`${index + 1}번째 행: 단위는 필수입니다.`);
            }
        });

        if (validationErrors.length > 0) {
            throw new Error('다음 오류를 수정해주세요:\n' + validationErrors.join('\n'));
        }

        // API 호출을 위한 데이터 변환
        const requestData = {
            qualityInspectionStandard: editedRows.map(row => ({
                qualityInspectionStandardId: parseInt(row.qualityInspectionStandardId),
                qualityInspectionId: parseInt(row.qualityInspectionId),
                targetValue: row.targetValue.toString().trim(),
                upperLimit: parseFloat(row.upperLimit),
                lowerLimit: parseFloat(row.lowerLimit),
                unit: row.unit.trim(),
                itemId: row.itemId.toString().trim()
            }))
        };

        const response = await fetch('/api/quality/standard', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestData)
        });

        const result = await response.json();

        if (result.status !== 200) {
            throw new Error(result.message);
        }

        return result;
    }

    const deleteStdData = async () => {
        const allData = stdGrid.getCheckedRows();
        // 체크된 행들 필터링
        const checkedRows = allData.filter(row => row.qualityInspectionStandardId);
        const notExistInDb = allData.filter(row => !row.qualityInspectionStandardId);

        if (allData.length === 0) {
            alert('삭제할 항목을 선택해주세요.');
            return;
        }

        if (checkedRows.length > 0) {
            try {
                // API 호출을 위한 데이터 변환
                const requestData = {
                    deleteList: checkedRows.map(row => ({
                        inspectionId: parseInt(row.qualityInspectionStandardId)
                    }))
                };

                const response = await fetch('/api/quality/standard', {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(requestData)
                });

                const result = await response.json();

                if (result.status === 200) {
                    alert('선택된 항목이 성공적으로 삭제되었습니다.');
                    stdGrid.removeCheckedRows();
                    getQualityStandards(); // 그리드 새로고침
                    return;
                } else {
                    alert('삭제 중 오류가 발생했습니다: ' + result.message);
                }
            } catch (error) {
                console.error('삭제 중 오류:', error);
                alert('삭제 중 오류가 발생했습니다: ' + error.message);
            }
        }

        if(notExistInDb.length > 0){
            stdGrid.removeCheckedRows();
            getQualityStandards();
        }

    }
}

window.onload = () => {
    init();
}

