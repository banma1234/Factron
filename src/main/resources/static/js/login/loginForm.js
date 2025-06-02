document.getElementById("login__button").addEventListener("click", function (e) {
    e.preventDefault();
    e.stopPropagation();

    fetchLoginData().then(res => {
        // ... 쿠키 세팅 코드 ...
        
        console.log(res);
    })
})

const fetchLoginData = async () => {
    const id = document.querySelector("input[name='employeeId']").value;
    const password = document.querySelector("input[name='password']").value;

    if (!id || !password) {
        alert("사원번호와 비밀번호를 모두 입력해주세요");
        return;
    }

    const data = {
        id: id,
        password: password
    };

    try {
        const res = await fetch(`/api/auth/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });
        return res.json();

    } catch (e) {
        console.error(e);
    }
}