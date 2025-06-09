package com.itwillbs.factron.service.employee;

import com.itwillbs.factron.common.component.AESUtil;
import com.itwillbs.factron.dto.employee.RequestEmployeeNewDTO;
import com.itwillbs.factron.dto.employee.RequestEmployeeSrhDTO;
import com.itwillbs.factron.dto.employee.ResponseEmployeeSrhDTO;
import com.itwillbs.factron.dto.employee.RequestEmployeeUpdateDTO;
import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.entity.IntergratAuth;
import com.itwillbs.factron.mapper.employee.EmployeeMapper;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import com.itwillbs.factron.repository.employee.IntergratAuthRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final IntergratAuthRepository intergratAuthRepository;
    private final EmployeeMapper employeeMapper;
    private final AESUtil aesUtil;

    /**
     * 검색 조건으로 사원 조회
     * @param requestEmployeeSrhDTO {@link RequestEmployeeSrhDTO}
     * @return List<ResponseEmployeeSrhDTO>
     */
    @Override
    public List<ResponseEmployeeSrhDTO> getEmployees(RequestEmployeeSrhDTO requestEmployeeSrhDTO){
        return employeeMapper.getEmployeeList(requestEmployeeSrhDTO)
                                    .stream()
                                    .map(dto ->
                                    {
                                        dto.removeTime();
                                        try {
                                            dto.setRrnBack(aesUtil.decrypt(dto.getRrnBack()));
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                        return dto;
                                    })
                                    .collect(Collectors.toList());
    }

    /**
     * 사원 정보 수정
     * @param reqEmployeeDTO {@link RequestEmployeeUpdateDTO}
     * @return Void
     */
    @Override
    @Transactional
    public Void updateEmployee(RequestEmployeeUpdateDTO reqEmployeeDTO) {
        Employee emp = employeeRepository.findById(reqEmployeeDTO.getEmpId()).orElseThrow(
                ()-> new EntityNotFoundException("사원번호: " + reqEmployeeDTO.getEmpId() + " 조회 결과가 없습니다.")
        );
        // RequestEmployeeUpdateDTO Validation 확인
        if (reqEmployeeDTO.getEmpName() == null || reqEmployeeDTO.getEmpName().isEmpty())
            reqEmployeeDTO.setEmpName(emp.getName());

        if (reqEmployeeDTO.getRrnBack() == null || reqEmployeeDTO.getRrnBack().isEmpty()){
            reqEmployeeDTO.setEmpName(emp.getRrnBack());
        }else{
            try {
                reqEmployeeDTO.setRrnBack(
                        aesUtil.encrypt(reqEmployeeDTO.getRrnBack())
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if(reqEmployeeDTO.getGender() == null || reqEmployeeDTO.getGender().isEmpty())
            reqEmployeeDTO.setGender(emp.getGender());
        if(reqEmployeeDTO.getEmail() == null || reqEmployeeDTO.getEmail().isEmpty())
            reqEmployeeDTO.setEmail(emp.getEmail());
        if(reqEmployeeDTO.getAddress() == null || reqEmployeeDTO.getAddress().isEmpty())
            reqEmployeeDTO.setAddress(emp.getAddress());
        if(reqEmployeeDTO.getEmpIsActive() == null || reqEmployeeDTO.getEmpIsActive().isEmpty())
            reqEmployeeDTO.setEmpIsActive("Y");
        reqEmployeeDTO.getEmpIsActive().toUpperCase();
        if(reqEmployeeDTO.getEmployeCode() == null || reqEmployeeDTO.getEmployeCode().isEmpty())
            reqEmployeeDTO.setEmployeCode(emp.getEmployCode());
        // 권한 확인!
        if(false)
            emp.updateTranfEmployeeInfo(reqEmployeeDTO);
        emp.updateNormEmployeeInfo(reqEmployeeDTO);
        return null;
    }

    /**
     * 새로운 사원 추가
     * @param reqEmployeeNewDTO {@link RequestEmployeeNewDTO}
     * @return Void
     */
    @Override
    @Transactional
    public Void addNewEmployee(RequestEmployeeNewDTO reqEmployeeNewDTO){
        // 새 아이디 생성
        Long newId = generateEmployeeId();

        // 주민번호 뒷자리 암호화
        try {
            reqEmployeeNewDTO.setRrnBack(aesUtil.encrypt(reqEmployeeNewDTO.getRrnBack()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Employee employee = reqEmployeeNewDTO.toEntity(newId, 1000L);
        employeeRepository.save(employee);

        Employee newEmp = employeeRepository.findById(newId).orElseThrow(
                ()-> new EntityNotFoundException("It does not exist")
        );

        IntergratAuth newIntergratAuth = reqEmployeeNewDTO.toIntergratAuth(newEmp);
        intergratAuthRepository.save(newIntergratAuth);
        return null;
    }

    // 공백 제거
    private String safeTrim(String input) {
        return (input == null) ? "" : input.trim();
    }

    // 재직여부 확인
    private String validActive(String str){
        String trimmed = safeTrim(str);
        if (trimmed.equals("")) return trimmed;
        return ((trimmed.equals("Y")  || trimmed.equals("N")) ) ?  trimmed: "";
    }

    // Code 유효성 검사

    private String validateCode(String code) {
        String trimmed = safeTrim(code);
        if (trimmed.matches("^[A-Z]{3}\\d{3}$")) {
            return trimmed;
        }
        return "";
    }

    private String dateParse (String dateTime){
        return dateTime.split("T")[0];
    }


    public Long generateEmployeeId() {
        LocalDate now = LocalDate.now();
        String yy = String.format("%02d", now.getYear() % 100); // 올해 2자리
        String mm = String.format("%02d", now.getMonthValue()); // 월 2자리

        String yearMonth = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        long countThisMonth = employeeRepository.countByYearMonth(yearMonth);
        String nnn = String.format("%04d", countThisMonth + 1); // 3자리로 채움

        String idStr = yy + mm + nnn;

        return Long.parseLong(idStr);
    }
}
