package com.example.demo.card.api.controller;

import com.example.demo.card.api.dto.AccessCardResponseDto;
import com.example.demo.card.api.dto.AccessCardSearchRequest;
import com.example.demo.card.api.dto.CreateAccessCardRequest;
import com.example.demo.card.mapper.AccessCardMapper;
import com.example.demo.card.service.AccessCardService;
import com.example.demo.card.service.model.AccessCardSearchCriteria;
import com.example.demo.common.api.dto.PageResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/access-cards")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
public class AccessCardController {
    private final AccessCardService accessCardService;
    private final AccessCardMapper mapper;

    @GetMapping("/{id}")
    public AccessCardResponseDto getAccessCardById(@PathVariable Long id){
        return mapper.toDto(accessCardService.findById(id));
    }

    @GetMapping(value = "/by-code", params = "cardCode")
    public AccessCardResponseDto getAccessCardByCode(@RequestParam String cardCode){
        return mapper.toDto(accessCardService.findByCode(cardCode));
    }

    @GetMapping
    public PageResponseDto<AccessCardResponseDto> search(
            @ModelAttribute AccessCardSearchRequest request,
            Pageable pageable
    ) {
        AccessCardSearchCriteria criteria = new AccessCardSearchCriteria(
                request.code(),
                request.status(),
                request.customerId()
        );

        return PageResponseDto.from(
                accessCardService.search(criteria, pageable
                ).map(mapper::toDto)
        );
    }

    @PostMapping
    public AccessCardResponseDto createAccessCard(@RequestBody @Valid CreateAccessCardRequest request){
        return mapper.toDto(accessCardService.create(request.code()));
    }

}
