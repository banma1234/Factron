const init = () => {
    const el = document.getElementById('processHistoryGrid');
    const data = {
        categories: [
            'Jan',
            'Feb',
            'Mar',
            'Apr',
            'May',
            'Jun',
            'Jul',
            'Aug',
            'Sep',
            'Oct',
            'Nov',
            'Dec',
        ],
        series: [
            {
                name: 'Seoul',
                data: [
                    [-8.3, 0.3],
                    [-5.8, 3.1],
                    [-0.6, 9.1],
                    [5.8, 16.9],
                    [11.5, 22.6],
                    [16.6, 26.6],
                    [21.2, 28.8],
                    [21.8, 30.0],
                    [15.8, 25.6],
                    [8.3, 19.6],
                    [1.4, 11.1],
                    [-5.2, 3.2],
                ],
            },
            {
                name: 'Busan',
                data: [
                    [0, 10],
                    [3.5, 13.1],
                    [5.6, 13.1],
                    [10.8, 16.9],
                    [11.5, 18.6],
                    [13.6, 20.6],
                    [15.2, 20.8],
                    [21.8, 26.0],
                    [17.8, 23.6],
                    [11.3, 16.6],
                    [4.4, 11.1],
                    [3.2, 11.2],
                ],
            },
        ],
    };
    const test = 1;
    const options = {
        chart: { title: test ? `${test}` : `test`, width: 1000, height: 400 },
        xAxis: { pointOnColumn: false, title: { text: '날짜' } },
        yAxis: { title: '수율' },
        series: { eventDetectType: 'grouped' },
    };



    const srhBtn = document.querySelector(`button[name="processSrhBtn"]`);

    if(srhBtn) {
        srhBtn.addEventListener("click", function (e) {
            e.preventDefault();
            // const processStatData = getProcessStat();
            const chart = toastui.Chart.areaChart({ el, data, options });

        })
    }


    const getProcessStat = async () => {
        const processNameOrId = document.querySelector("input[name='processNameOrId']").value;

        // params 생성
        const params = new URLSearchParams();

        // params에 검색어 추가
        if(name) params.append("processNameOrId", processNameOrId)

        // 사원 목록 API 호출
        fetch(`/api/process/history/statistics?${params.toString()}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).then(res => res.json())
            .then(res => {
                if(res.status === 200){
                    return res.data; // grid에 세팅
                }else{
                    alert(res.message);
                }
                return {};
            })
            .catch(e => {
                alert(e);
            });
    }
}

window.onload = () => {
    init();
}