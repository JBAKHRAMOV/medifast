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
    public static List<ComplaintsDTO> COMPLAINTS_LIST = new LinkedList<>(Arrays.asList(
            new ComplaintsDTO(1L, "Bosh og’riq", "Головная боль", "Headache"),
            new ComplaintsDTO(2L, "Kekirish", "Заикание", "Stuttering"),
            new ComplaintsDTO(3L, "Og’izda hid", "Зловонное дыхание", "Bad breath"),
            new ComplaintsDTO(4L, "Ko’ngil aynash", "Тошнота", "Nausea"),
            new ComplaintsDTO(5L, "Qusish", "Рвота", "Vomiting"),
            new ComplaintsDTO(6L, "Ich ketish", "Диарея", "Diarrhea"),
            new ComplaintsDTO(7L, "Jigʻildon qaynashi", "Изжога", "heartburn"),
            new ComplaintsDTO(8L, "Ich qotishi", "Запор", "Constipation"),
            new ComplaintsDTO(9L, "Tushkunlik", "Депрессия", "tajang"),
            new ComplaintsDTO(10L, "oyoq shishi", "отек ноги", "leg swelling"),
            new ComplaintsDTO(11L, "Nafas qisilishi", "Одышка", "Dyspnea"),
            new ComplaintsDTO(12L, "Kechqurun siyish", "Мочеиспускание вечером", "Evening urination"),
            new ComplaintsDTO(13L, "Siyganda achish", "Открытие при мочеиспускании", "Urinary opening"),
            new ComplaintsDTO(14L, "Xira siydik", "Темная моча", "Dark urine"),
            new ComplaintsDTO(15L, "siydik hidli", "запах мочи", "smelly urine")
    ));
}
