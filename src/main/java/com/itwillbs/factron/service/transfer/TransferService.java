package com.itwillbs.factron.service.transfer;

import com.itwillbs.factron.dto.transfer.RequestTransferDTO;
import com.itwillbs.factron.dto.transfer.ResponseTransferDTO;

import java.util.List;

public interface TransferService {

    // 인사발령 목록 조회
    List<ResponseTransferDTO> getTransferList(RequestTransferDTO requestTransferDTO);

    Void registTransfer(RequestTransferDTO requestTransferDTO);
}
