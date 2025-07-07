package com.itwillbs.factron.controller.machine;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.machine.RequestAddMachineDTO;
import com.itwillbs.factron.dto.machine.RequestMachineInfoDTO;
import com.itwillbs.factron.dto.machine.RequestUpdateMachineDTO;
import com.itwillbs.factron.dto.machine.ResponseMachineInfoDTO;
import com.itwillbs.factron.service.machine.MachineService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/machine")
@RequiredArgsConstructor
public class MachineRestController {

    private final MachineService machineService;

    // 설비 추가 API
    @PostMapping()
    public ResponseDTO<Void> addProcess(@RequestBody @Valid RequestAddMachineDTO requestDto) {

        try {

            machineService.addMachine(requestDto);

            return ResponseDTO.success("설비를 추가하였습니다", null);
        } catch (EntityNotFoundException e) {

            return ResponseDTO.fail(800, e.getMessage(), null);
        } catch (IllegalStateException e) {

            return ResponseDTO.fail(801, e.getMessage(), null);
        }
    }

    // 설비 수정 API
    @PutMapping()
    public ResponseDTO<Void> updateMachine(@RequestBody @Valid RequestUpdateMachineDTO requestDto) {

        try {

            machineService.updateMachine(requestDto);

            return ResponseDTO.success("설비를 수정하였습니다", null);
        } catch (EntityNotFoundException e) {

            return ResponseDTO.fail(800, e.getMessage(), null);
        }
    }

    // 설비 목록 조회 API
    @GetMapping()
    public ResponseDTO<List<ResponseMachineInfoDTO>> getMachineList(
            @ModelAttribute RequestMachineInfoDTO requestDto) {

        return ResponseDTO.success(machineService.getMachineList(requestDto));
    }
}
