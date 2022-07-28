package com.company.bot.service;

import com.company.bot.dto.ComplaintsDTO;
import com.company.bot.entity.ComplaintsEntity;
import com.company.bot.repository.ComplaintsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
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
            new ComplaintsDTO(15L, "Siydik hidli", "запах мочи", "smelly urine"),
            new ComplaintsDTO(16L, "Ko'p siyish", "Много мочеиспускания", "Lots of urination"),
            new ComplaintsDTO(17L, "Yurak o’ynashi", "Сердце играет", "playing heart"),
            new ComplaintsDTO(18L, "Ko’krakda og’riq", "Грудная боль", "Chest pain"),
            new ComplaintsDTO(19L, "Burun bitishi", "Насморк", "Runny nose"),
            new ComplaintsDTO(20L, "Burun oqishi", "Носовые выделения", "Nasal discharge"),
            new ComplaintsDTO(21L, "Yo'tal", "кашель", "cough"),
            new ComplaintsDTO(22L, "Qo'l titrashi", "Тремор рук", "Hand tremors"),
            new ComplaintsDTO(23L, "Bel og’riq", "болезнь спины", "back pain"),
            new ComplaintsDTO(24L, "Bo’g’imlarda og’riq", "Боль в суставах", "Joint pain"),
            new ComplaintsDTO(25L, "Havo yetishmasligi", "Недостаток воздуха", "Lack of air"),
            new ComplaintsDTO(26L, "Uyqu yomon", "плохой сон", "bad son"),
            new ComplaintsDTO(27L, "Xotira yomon", "Плохая память", "Bad memory"),
            new ComplaintsDTO(28L, "Hayzni buzilishi", "Менструальные расстройства", "Menstrual disorders"),
            new ComplaintsDTO(29L, "Vazn oshgan", "Пополнел", "Gained weight"),
            new ComplaintsDTO(30L, "Vazn kamaygan", "Вес уменьшился", "Weight loss"),
            new ComplaintsDTO(31L, "Ko'rish pasayishi", "Снижение зрения", "Decreased vision"),
            new ComplaintsDTO(32L, "Jinsiy zaiflik", "Сексуальная импотенция", "sexual impotence"),
            new ComplaintsDTO(33L, "Qo’l/oyoq uvishi", "Онемение рук/ног", "Numbness of hands/feet"),
            new ComplaintsDTO(34L, "Holsizlik", "Усталость", "Fatigue"),
            new ComplaintsDTO(35L, "Terlash", "Потоотделение", "Sweating"),
            new ComplaintsDTO(36L, "Yurak siqilishi", "Учащенное сердцебиение", "Cardiopalmus")
    ));

    public void fieldSave(List<ComplaintsDTO> dtoList, Long id) {
        var entityList = complaintsRepository.findAllByUserId(id);

        if (entityList.isEmpty()) {

            entitySave(dtoList, id);
        } else {
            complaintsRepository.deleteAll(entityList);
            entitySave(dtoList, id);
        }

    }

    private void entitySave(List<ComplaintsDTO> dtoList, Long id) {
        var entity = new ComplaintsEntity();
        entity.setUserId(id);

        for (ComplaintsDTO dto : dtoList) {
            entity.setKey(dto.getKey());
            entity.setNameRu(dto.getNameRu());
            entity.setNameUz(dto.getNameUz());
            entity.setCreatedDate(LocalDate.now());
            complaintsRepository.save(entity);
        }
    }
}
