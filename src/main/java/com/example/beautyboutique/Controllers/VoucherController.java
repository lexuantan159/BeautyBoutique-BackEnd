package com.example.beautyboutique.Controllers;

import com.example.beautyboutique.DTOs.Responses.Voucher.AllVouchers;
import com.example.beautyboutique.DTOs.Responses.Voucher.PageVoucher;
import com.example.beautyboutique.DTOs.Responses.Voucher.VoucherOfUser;
import com.example.beautyboutique.Models.User;
import com.example.beautyboutique.Models.Voucher;
import com.example.beautyboutique.Models.VoucherDetail;
import com.example.beautyboutique.Services.JWTServiceImpl;
import com.example.beautyboutique.Services.User.UserService;
import com.example.beautyboutique.Services.Voucher.VoucherServices;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/voucher")
@CrossOrigin("*")
public class VoucherController {
    @Autowired
    VoucherServices voucherServices;
    @Autowired
    UserService userService;
    @Autowired
    JWTServiceImpl jwtService;


    @GetMapping(value = "/get-voucher")

    public ResponseEntity<?> getVouchers(HttpServletRequest requestToken) {
        try {
            Integer userId = jwtService.getUserIdByToken(requestToken);
            if (userId == null) {
                List<Voucher> listVouchers = voucherServices.getAllVouchers();
                return new ResponseEntity<>(new AllVouchers(0, "Get vouchers successfully!", listVouchers), HttpStatus.OK);
            }
            List<VoucherDetail> listVouchers = voucherServices.getListVouchersByUserId(userId);
            return new ResponseEntity<>(new VoucherOfUser(0, "Get vouchers of user successfully!", listVouchers), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Get vouchers fail!", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "add-voucher")
    public ResponseEntity<?> addVoucher(HttpServletRequest requestToken,
                                        @RequestParam(value = "voucherId", required = false) Integer voucherId) {
        try {
            Integer userId = jwtService.getUserIdByToken(requestToken);
            Boolean isAddVoucher = voucherServices.addVoucher(userId, voucherId);
            if (!isAddVoucher)
                return new ResponseEntity<>("Add vouchers successfully!", HttpStatus.CREATED);

            return new ResponseEntity<>("Add vouchers fail!", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Add voucher fail!", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/get-all-voucher")
    public ResponseEntity<?> getAllVoucher() {
        try {
            List<Voucher> vouchersList = voucherServices.getAllVouchers();
            return new ResponseEntity<>(new PageVoucher("Get All Voucher Successfully", vouchersList.size(), vouchersList), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Retrieving information failed", HttpStatus.BAD_REQUEST);
        }
    }
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PostMapping(value = "/create-voucher", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE

    }, produces = {MediaType.APPLICATION_JSON_VALUE
    })
    @ResponseBody
    ResponseEntity<?> createVoucher(Voucher voucher) {
        try {
            LocalDate startDate = voucher.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate = voucher.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (startDate.isBefore(endDate)) {
                if (endDate.minusDays(1).isAfter(startDate)) {
                    Voucher createdVoucher = voucherServices.createVoucher(voucher);
                    if (createdVoucher != null) {
                        return new ResponseEntity<>("Created a voucher successfully", HttpStatus.CREATED);
                    } else {
                        return new ResponseEntity<>("Failed to create voucher", HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<>("Start date and end date must be at least 1 day apart", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("End date must be after start date", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(value = "/save-voucher-for-user")
    ResponseEntity<?> saveVoucher(@RequestParam(value = "voucherId") Integer voucherId,
                                  HttpServletRequest requestToken) {

        try {
            Integer userId = jwtService.getUserIdByToken(requestToken);
            boolean isSave = voucherServices.getVoucherDetailByUserIdAndVoucherId(userId, voucherId);
            if (isSave) {
                return new ResponseEntity<>("You have already saved this voucher", HttpStatus.OK);
            }
            Optional<User> userSaveVoucher = userService.getUserById(userId);
            Voucher voucher = voucherServices.getAVoucherId(voucherId);
            Integer remainAmount = voucher.getQuantity() - voucher.getNumUsedVoucher();

            if (remainAmount <= 0) {
                return new ResponseEntity<>("Vouchers are sold out", HttpStatus.OK);
            }

            VoucherDetail voucherDetail = new VoucherDetail();
            voucherDetail.setUser(userSaveVoucher.get());
            voucherDetail.setVoucher(voucher);
            VoucherDetail createdVoucher = voucherServices.saveVoucherForUser(voucherDetail);
            if (createdVoucher != null) {
                return new ResponseEntity<>("Save voucher for user successfully", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Failed to save voucher for user", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal server failed", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/update-voucher", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE
    }, produces = {MediaType.APPLICATION_JSON_VALUE
    })
    @ResponseBody
    ResponseEntity<?> updateVoucher(Voucher voucher,
                                    @RequestParam(value = "id") Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("Invalid voucher ID");
            }
            Voucher updatedVoucherResult = voucherServices.updateVoucher(id, voucher);
            if (updatedVoucherResult != null) {
                return new ResponseEntity<>("Updated Voucher successfully", HttpStatus.OK);
            }
            return new ResponseEntity<>("Failed to update comment", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server!", HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @DeleteMapping(value = "/delete-voucher")
    public ResponseEntity<?> deleteComment(@RequestParam(value = "id") Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("Invalid comment ID");
            }
            boolean isDelete = voucherServices.deleteVoucher(id);
            if (isDelete) {
                return new ResponseEntity<>("Delete voucher successfully!", HttpStatus.OK);
            }
            return new ResponseEntity<>("Delete voucher fail!", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.printf(e.getMessage());
            return new ResponseEntity<>("Internal server failed", HttpStatus.BAD_REQUEST);
        }
    }


}
