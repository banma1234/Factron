package com.itwillbs.factron.service.storage;

import com.itwillbs.factron.dto.storage.ResponseStorageDTO;
import com.itwillbs.factron.repository.storage.StorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StorageServiceImpl implements StorageService {

    private final StorageRepository storageRepository;

    @Override
    public List<ResponseStorageDTO> getStorageByName(String name) {
        return storageRepository.findByName(name);
    }
}
