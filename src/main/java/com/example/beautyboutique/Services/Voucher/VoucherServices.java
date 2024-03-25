package com.example.beautyboutique.Services.Voucher;

import com.example.beautyboutique.Models.User;
import com.example.beautyboutique.Models.Voucher;
import com.example.beautyboutique.Models.VoucherDetail;
import com.example.beautyboutique.Repositories.UserRepository;
import com.example.beautyboutique.Repositories.VoucherDetailRepository;
import com.example.beautyboutique.Repositories.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VoucherServices implements VoucherService {
    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    VoucherDetailRepository voucherDetailRepository;


    @Autowired
    UserRepository userRepository;


    @Override
    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    @Override
    public Boolean addVoucher(Integer userId, Integer voucherId) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty())
                return false;
            Optional<Voucher> voucherOptional = voucherRepository.findById(voucherId);
            if (voucherOptional.isEmpty())
                return false;

            User user = userOptional.get();
            Voucher voucher = voucherOptional.get();

            VoucherDetail voucherDetail = new VoucherDetail(user, voucher);
            voucherDetailRepository.save(voucherDetail);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public List<VoucherDetail> getListVouchersByUserId(Integer userId) {
        return voucherDetailRepository.findByUserId(userId);
    }

    @Override
    public Voucher getAVoucherId(Integer id) {
        try {
            Optional<Voucher> optionalVoucher = voucherRepository.findById(id);
            if (optionalVoucher.isPresent()) {
                return optionalVoucher.get();
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Voucher();
        }
    }

    @Override
    public boolean deleteVoucher(Integer id) {
        try {
            Optional<Voucher> optionalVoucher = voucherRepository.findById(id);
            if (optionalVoucher.isPresent()) {
                voucherRepository.deleteById(id);
                System.out.println("Comment with id " + id + " have been deleted.");
                return true;
            } else {
                System.out.println("Comment not found with id: " + id);
                return false;
            }
        } catch (Exception e) {
            System.out.println("An error occurred while deleting the comment: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean getVoucherDetailByUserIdAndVoucherId(Integer userId, Integer voucherId) {
        try {
            Optional<VoucherDetail> voucherDetail = voucherDetailRepository.findByUserIdAndVoucherId(userId, voucherId);
            if (voucherDetail.isPresent()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println(" error : " + e.getMessage());
            return false;
        }

    }

    @Override
    public Voucher createVoucher(Voucher voucher) {

        return voucherRepository.save(voucher);
    }

    @Override
    public VoucherDetail saveVoucherForUser(VoucherDetail voucherDetail) {
        return voucherDetailRepository.save(voucherDetail);
    }

    @Override
    public Voucher updateVoucher(Integer id, Voucher updateVoucher) {
        try {
            Optional<Voucher> optionalVoucher = voucherRepository.findById(id);
            if (optionalVoucher.isPresent()) {
                Voucher existingVoucher = optionalVoucher.get();
                if (updateVoucher.getContent() != null) {
                    existingVoucher.setContent(updateVoucher.getContent());
                }
                if (updateVoucher.getTitle() != null) {
                    existingVoucher.setTitle(updateVoucher.getTitle());
                }
                if (updateVoucher.getNumUsedVoucher() != null) {
                    existingVoucher.setNumUsedVoucher(updateVoucher.getNumUsedVoucher());
                }
                if (updateVoucher.getStartDate() != null) {
                    existingVoucher.setStartDate(updateVoucher.getStartDate());
                }
                if (updateVoucher.getEndDate() != null) {
                    existingVoucher.setEndDate(updateVoucher.getEndDate());
                }
                if (updateVoucher.getQuantity() != null) {
                    existingVoucher.setQuantity(updateVoucher.getQuantity());
                }
                if (updateVoucher.getDiscount() != null) {
                    existingVoucher.setDiscount(updateVoucher.getDiscount());
                }
                if (updateVoucher.getMinimumOrder() != null) {
                    existingVoucher.setMinimumOrder(updateVoucher.getMinimumOrder());
                }
                if (updateVoucher.getMaximDiscount() != null) {
                    existingVoucher.setMaximDiscount(updateVoucher.getMaximDiscount());
                }
                return voucherRepository.save(existingVoucher);
            } else {
                System.out.println("Comment not found with id: " + id);
            }
        } catch (Exception e) {
            System.out.println("An error occurred while updating the Comment: " + e.getMessage());
        }
        return null;
    }
}
