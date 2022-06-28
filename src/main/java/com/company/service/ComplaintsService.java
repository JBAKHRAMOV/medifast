package com.company.service;

import com.company.dto.ComplaintsDTO;
import com.company.repository.ComplaintsRepository;
import lombok.RequiredArgsConstructor;
import org.jvnet.hk2.annotations.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintsService {
    private final ComplaintsRepository complaintsRepository;
    public static List<ComplaintsDTO> COMPLAINTS_LIST =new LinkedList<>(Arrays.asList(
            new ComplaintsDTO(1L, "bosh og'rigi UZ","bosh og'rigi RU", "bosh"),
            new ComplaintsDTO(2L, "kekirish UZ","kekirish RU", "kek" ),
            new ComplaintsDTO(3L, "ogizda hid UZ","ogizda hid RU", "ogiz" ),
            new ComplaintsDTO(4L, "kongil aynishi UZ","kongil aynishi RU", "kongil" ),
            new ComplaintsDTO(5L, "oyoq shishi UZ","oyoq shishi RU", "oyoq" ),
            new ComplaintsDTO(6L, "yotal UZ","yotal RU", "yotal" ),
            new ComplaintsDTO(7L, "uyqu UZ","uyqu RU",  "uyqu"),
            new ComplaintsDTO(8L, "terlashi UZ","terlash RU", "terlash" ),
            new ComplaintsDTO(9L, "yurak UZ","tajang RU",  "tajang"),
            new ComplaintsDTO(10L, "korish UZ","korish RU", "korish" ),
            new ComplaintsDTO(11L, "bel og'rigi UZ","bel og'rigi RU","bel"  )
    ));
}
