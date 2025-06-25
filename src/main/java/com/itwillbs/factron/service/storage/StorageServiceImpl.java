package com.itwillbs.factron.service.storage;

import com.itwillbs.factron.dto.storage.ResponseStorageDTO;
import com.itwillbs.factron.entity.Storage;
import com.itwillbs.factron.repository.storage.StorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StorageServiceImpl implements StorageService {

    private final StorageRepository storageRepository;

    @Override
    public List<ResponseStorageDTO> getStorageByName(String name) {

        List<Storage> storageList;

        if(name == null || name.isEmpty()) {
            storageList = storageRepository.findAll();
            System.out.println(">>>>>>>>>>>>>>>>>>>>>> : " + storageList.size());
        } else {
            storageList = storageRepository
                    .findByName(name)
                    .orElseThrow(() -> new NoSuchElementException("해당 창고는 존재하지 않습니다"));
        }

        return toStorageDTOList(storageList);
    }

    private List<ResponseStorageDTO> toStorageDTOList(List<Storage> entity) {

        return entity.stream()
                .map(ResponseStorageDTO :: fromEntity)
                .toList();
    }
}
