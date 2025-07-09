// 대문자 변환 함수
const toUpperCase = (str) => {
    if (typeof str !== 'string') return '';
    return str.toUpperCase();
}

// 날짜 포맷팅 함수 (날짜만)
const formatDate = (date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

const init = () => {
    // 기본 날짜 설정 (최근 30일)
    const endDate = new Date();
    const startDate = new Date();
    startDate.setDate(startDate.getDate() - 30);
    
    document.getElementById('startDate').value = formatDate(startDate);
    document.getElementById('endDate').value = formatDate(endDate);

    // 기간 선택 이벤트
    document.getElementById('periodSelect').addEventListener('change', function() {
        const days = parseInt(this.value);
        if (days) {
            const endDate = new Date();
            const startDate = new Date();
            startDate.setDate(startDate.getDate() - days);
            
            document.getElementById('startDate').value = formatDate(startDate);
            document.getElementById('endDate').value = formatDate(endDate);
        }
    });

    // 검색 버튼 이벤트
    document.getElementById('searchBtn').addEventListener('click', function() {
        const processNameOrId = document.getElementById('processNameOrId').value;
        if (!processNameOrId) {
            alert('공정명 또는 ID를 입력해주세요.');
            return;
        }
        // 검색 버튼으로 호출할 때는 processName을 null로 전달
        getProcessStat(processNameOrId, null);
    });

    const lineGrid = initGrid(
        document.getElementById('line_grid'),
        300,
        [
            {
                header: '번호',
                name: 'id',
                hidden: true
            },
            {
                header: '라인 번호',
                name: 'lineId',
                align: 'center'
            },
            {
                header: '라인명',
                name: 'lineName',
                align: 'center'
            },
            {
                header: '라인 가동 상태',
                name: 'lineStatusName',
                align: 'center'
            },
            {
                header: '등록 일자',
                name: 'createdAt',
                align: 'center'
            },
            {
                header: '등록자 사번',
                name: 'createdBy',
                align: 'center'
            }
        ]
    );

    getLines();

    const lineProcessGrid = initGrid(
        document.getElementById('line_process_grid'),
        200,
        [
            {
                header: '번호',
                name: 'id',
                hidden: true
            },
            {
                header: '공정번호',
                name: 'processId',
                align: 'center'
            },
            {
                header: '공정명',
                name: 'processName',
                align: 'center'
            },
            {
                header: '공정유형',
                name: 'processTypeName',
                align: 'center'
            },
            {
                header: '소속 라인',
                name: 'lineName',
                align: 'center'
            },
            {
                header: '공정 시간',
                name: 'standardTime',
                align: 'center',
                formatter: (value) => {
                    return value.value ? `${value.value}분` : '';
                }
            },
            {
                header: '설비 유무',
                name: 'hasMachine',
                align: 'center',
                formatter: (value) => {
                    const upperValue = toUpperCase(value.value)
                    return `${upperValue==='Y' ? '보유' : '미보유'}`;
                }
            }
        ]
    );

    // 라인 선택 시 해당 라인에 소속된 공정 목록 표시
    lineGrid.on('click', (e) => {
        const rowKey = e.rowKey;
        const rowData = lineGrid.getRow(rowKey);

        if (rowData && rowData.lineId) {
            getLineProcesses(rowData.lineId, rowData.lineName);
        }
    });

    // 공정 선택 시 통계 조회
    lineProcessGrid.on('click', (e) => {
        const rowData = lineProcessGrid.getRow(e.rowKey);
        console.log(rowData.processId);
        document.getElementById('processNameOrId').value = rowData.processId;
        getProcessStat(rowData.processId, rowData.processName);
    });

    const el = document.getElementById('processHistoryGrid');
    
    // 기본 차트 옵션 설정
    const options = {
        chart: { title: '수율 변동성 분석', width: 1000, height: 400 },
        xAxis: { pointOnColumn: false, title: { text: '날짜' } },
        yAxis: { title: '수율 (%)' },
        series: { eventDetectType: 'grouped' },
    };

    const chart = toastui.Chart.areaChart({ el, data: {
            categories: [],
            series: []
        }, options });

    // 라인 목록 조회
    async function getLines() {
        try {
            const response = await fetch(`/api/line`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            });
            
            const result = await response.json();
            
            if(result.status === 200){
                console.log(result.data);
                lineGrid.resetData(result.data);
            } else {
                alert(result.message);
                lineGrid.resetData([]);
            }
        } catch (e) {
            alert(e);
            lineGrid.resetData([]);
        }
    }

    // 선택한 라인에 소속된 공정 목록 조회
    async function getLineProcesses(lineId, lineName) {
        const params = new URLSearchParams();
        params.append("lineId", lineId);

        try {
            const response = await fetch(`/api/process?${params.toString()}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            });
            
            const result = await response.json();
            
            if(result.status === 200){
                lineProcessGrid.resetData(result.data);
            } else {
                alert(result.message);
                lineProcessGrid.resetData([]);
            }
        } catch (e) {
            console.error("공정 목록 조회 실패:", e);
            alert("공정 목록을 불러오는데 실패했습니다.");
            lineProcessGrid.resetData([]);
        }
    }

    const getProcessStat = async (processId, processName) => {
        const processNameOrId = processId;
        
        if (!processNameOrId) {
            alert('공정명 또는 ID를 입력해주세요.');
            return;
        }

        // 날짜 값 가져오기
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;

        if (!startDate || !endDate) {
            alert('시작 날짜와 종료 날짜를 모두 입력해주세요.');
            return;
        }

        // 날짜를 LocalDateTime으로 변환 (시작일은 00:00:00, 종료일은 23:59:59)
        const startDateTime = startDate + 'T00:00:00';
        const endDateTime = endDate + 'T23:59:59';

        // params 생성
        const params = new URLSearchParams();
        params.append("processNameOrId", processNameOrId);
        params.append("startDate", startDateTime);
        params.append("endDate", endDateTime);

        try {
            // 공정 통계 API 호출
            const response = await fetch(`/api/process/history/statistics?${params.toString()}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            });
            
            const result = await response.json();
            
            if(result.status === 200 ){
                // 차트 데이터 변환
                const chartData = transformDataForChart(result.data) || [];
                
                // 차트 제목 설정 로직 개선
                let chartTitle = "수율 변동성 분석";
                
                if (processName) {
                    // 공정명이 있는 경우 (그리드에서 클릭한 경우)
                    chartTitle = processName + " 수율";
                } else {
                    // 검색으로 입력한 경우, 입력값을 그대로 사용
                    const inputValue = document.getElementById('processNameOrId').value;
                    if (inputValue) {
                        chartTitle = inputValue + " 수율";
                    }
                }

                const chartTitleEl = document.querySelector('.chart_title');
                chartTitleEl.innerText = chartTitle;

                // 차트 생성
                chart.setData(chartData);
                
            } else {
                console.error('API 오류:', result.message);
                alert('데이터 조회 중 오류가 발생했습니다: ' + result.message);
            }
            
        } catch (error) {
            console.error('API 호출 오류:', error);
            alert('API 호출 중 오류가 발생했습니다: ' + error.message);
        }
    }

    // API 데이터를 차트 형식으로 변환하는 함수
    const transformDataForChart = (apiData) => {
        if (!apiData || apiData.length === 0) {
            console.warn('변환할 데이터가 없습니다.');
            return {
                categories: [],
                series: []
            };
        }

        // 날짜별로 데이터 정렬
        const sortedData = apiData.sort((a, b) => new Date(a.workDate) - new Date(b.workDate));
        
        // 특정 조건으로 필터링 (수율, 이동평균, 이동표준편차가 모두 0이 아닌 데이터만)
        const filteredData = sortedData.filter(item => 
            item.yieldRate > 0 &&
            item.yieldRate &&
            item.movingAverageYieldRate > 0 &&
            item.movingAverageYieldRate &&
            item.movingStddevYieldRate > 0 &&
            item.movingStddevYieldRate
        );
        
        // 카테고리 (날짜) 추출
        const categories = filteredData.map(item => {
            const date = new Date(item.workDate);
            return `${date.getMonth() + 1}/${date.getDate()}`;
        });

        // 시리즈 데이터 생성
        const series = [
            {
                name: '실제 수율',
                data: filteredData.map(item => {
                    return [item.yieldRate, item.yieldRate]
                })
            },
            {
                name: '이동 평균 수율',
                data: filteredData.map(item => {
                    const avg = item.movingAverageYieldRate || 0;
                    const stddev = item.movingStddevYieldRate || 0;
                    // 범위 데이터: [상한값, 하한값] 형태
                    return [avg + stddev * 2, avg - stddev];
                })
            }
        ];

        console.log('변환된 차트 데이터:', { categories, series });
        console.log('필터링된 데이터 개수:', filteredData.length);
        console.log('원본 데이터 개수:', sortedData.length);

        return {
            categories,
            series
        };
    }
}

window.onload = () => {
    init();
}