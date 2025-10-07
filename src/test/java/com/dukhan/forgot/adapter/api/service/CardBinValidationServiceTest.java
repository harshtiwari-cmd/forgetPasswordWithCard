//package com.dukhan.forgot.adapter.api.service;
//
//import com.dukhan.forgot.adapter.api.service.impl.CardBinValidationServiceImpl;
//import com.dukhan.forgot.domain.model.dto.CardBinValidationRequest;
//import com.dukhan.forgot.domain.model.dto.CardBinValidationResponse;
//import com.dukhan.forgot.domain.model.entity.CardBinMaster;
//import com.dukhan.forgot.domain.repository.CardBinMasterRepository;
//import com.dukhan.forgot.infrastructure.common.AppConstant;
//import com.dukhan.forgot.infrastructure.common.GenericResponse;
//import com.dukhan.forgot.infrastructure.common.hsm.HSMEncryptorManagerImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class CardBinValidationServiceTest {
//
//    @Mock
//    private CardBinMasterRepository repository;
//
//    @Mock
//    private HSMEncryptorManagerImpl hsmEncryptor;
//
//    @InjectMocks
//    private CardBinValidationServiceImpl validationService;
//
//    private CardBinValidationRequest request;
//
//    @BeforeEach
//    void setUp() {
//        request = new CardBinValidationRequest();
//        request.setCardNumber("4203741234567890"); // BIN prefix match
//        request.setPin("1234");
//    }
//
//
//    @Test
//    public void shouldReturnActiveBins() {
//
//        CardBinMaster bin1 = new CardBinMaster("401", "420374", "ISLAMIC PLATINUM", "CREDIT", "ACTIVE");
//        CardBinMaster bin2 = new CardBinMaster("900", "527158", "MC PRESTIGE DEBT", "DEBIT", "ACTIVE");
//        List<CardBinMaster> binList = List.of(bin1, bin2);
//
//        Mockito.when(repository.findAllActive()).thenReturn(binList);
//
//        GenericResponse<List<CardBinMaster>> activeBins = validationService.getActiveBins();
//
//        assertNotNull(activeBins);
//        assertEquals("401", activeBins.getData().get(0).getCode());
//        assertEquals("420374", activeBins.getData().get(0).getBin());
//        assertEquals("ISLAMIC PLATINUM", activeBins.getData().get(0).getProductType());
//        assertEquals("000000", activeBins.getStatus().getCode());
//
//    }
//
//    @Test
//    void shouldValidateCardAndEncryptPinSuccessfully() {
//        request.setCardNumber("4203741234567890");
//        request.setPin("1234");
//
//        CardBinMaster bin = new CardBinMaster("401", "420374", "ISLAMIC PLATINUM", "CREDIT", "ACTIVE");
//        when(repository.findByBin(anyString())).thenReturn(List.of(bin));
//        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString()))
//                .thenReturn("encryptedPin123");
//
//        GenericResponse<CardBinValidationResponse> response = validationService.validateCardBin(
//                "unit1", "channel1", "en", "svc1", "scr1", "mod1", "submod1", request);
//
//        assertNotNull(response);
//        assertEquals("000000", response.getStatus().getCode());
//        assertEquals("420374", response.getData().getBin());
//        assertEquals("ISLAMIC PLATINUM", response.getData().getProductType());
//        assertEquals("CREDIT", response.getData().getCardType());
//        assertEquals("401", response.getData().getCode());
//        assertEquals("encryptedPin123", response.getData().getEncryptedPin());
//        assertTrue(response.getData().isValid());
//    }
//
//
//    @Test
//    void shouldReturnErrorWhenCardNumberIsNull() {
//        request.setCardNumber(null);
//
//        GenericResponse<CardBinValidationResponse> response = validationService.validateCardBin(
//                "unit1", "channel1", "en", "svc1", "scr1", "mod1", "submod1", request);
//
//        assertNotNull(response);
//        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
//        assertNull(response.getData());
//    }
//
//    @Test
//    void shouldReturnErrorWhenHSMEncryptionFails() {
//        request.setCardNumber("4203741234567890");
//        request.setPin("1234");
//
//        CardBinMaster bin = new CardBinMaster("401", "420374", "ISLAMIC PLATINUM", "CREDIT", "ACTIVE");
//        when(repository.findByBin(anyString())).thenReturn(List.of(bin));
//        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString()))
//                .thenThrow(new RuntimeException("HSM encryption failed"));
//
//        GenericResponse<CardBinValidationResponse> response = validationService.validateCardBin(
//                "unit1", "channel1", "en", "svc1", "scr1", "mod1", "submod1", request);
//
//        assertNotNull(response);
//        assertEquals("HSM-001", response.getStatus().getCode());
//        assertTrue(response.getStatus().getDescription().contains("PIN encryption failed"));
//        assertNull(response.getData());
//    }
//
//}
