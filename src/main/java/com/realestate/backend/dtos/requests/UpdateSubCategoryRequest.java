package com.realestate.backend.dtos.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubCategoryRequest {

    private Long id;
    private String name;
    private Long categoryId;
}