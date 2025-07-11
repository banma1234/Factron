// 로그인 페이지 JavaScript
document.addEventListener('DOMContentLoaded', function() {
    
    // 인사권한 로그인 버튼 클릭 이벤트
    document.querySelector('button[name="hotkey_hr"]').addEventListener('click', function() {
        // 인사권한 로그인 정보
        const employeeId = '25070002';
        const password = '1234';
        
        // 폼 필드에 값 설정
        document.querySelector('input[name="employeeId"]').value = employeeId;
        document.querySelector('input[name="password"]').value = password;
        
        // 폼 제출
        document.querySelector('.login__form').submit();
    });
    
    // 관리자 로그인 버튼 클릭 이벤트
    document.querySelector('button[name="hotkey_admin"]').addEventListener('click', function() {
        // 관리자 로그인 정보
        const employeeId = '25070001';
        const password = '1234';
        
        // 폼 필드에 값 설정
        document.querySelector('input[name="employeeId"]').value = employeeId;
        document.querySelector('input[name="password"]').value = password;
        
        // 폼 제출
        document.querySelector('.login__form').submit();
    });
}); 