package com.example.beautyboutique.Controllers;


import com.example.beautyboutique.DTOs.Requests.ShipDetail.ShipDetailDTO;
import com.example.beautyboutique.DTOs.Responses.ResponseDTO;
import com.example.beautyboutique.DTOs.Responses.ResponseMessage;
import com.example.beautyboutique.DTOs.Responses.ShipDetail.ResponseShipDetails;
import com.example.beautyboutique.Models.ShipDetail;
import com.example.beautyboutique.Models.User;
import com.example.beautyboutique.Services.JWTService;
import com.example.beautyboutique.Services.JWTServiceImpl;
import com.example.beautyboutique.Services.ShipDetail.ShipDetailServiceImpl;
import com.example.beautyboutique.Services.User.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.beautyboutique.Utils.ZaloAlgorithem.JwtUtil;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/ship-detail")
public class ShipDetailController {

    @Autowired
    ShipDetailServiceImpl shipDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;

    @GetMapping(value = "/get-all")
    public ResponseEntity<?> getShipDetails(HttpServletRequest request) {
        Integer userId = jwtService.getUserIdByToken(request);
        try {
            List<ShipDetail> shipDetails = shipDetailService.getShipDetailList(userId);
            return new ResponseEntity<>(new ResponseShipDetails(shipDetails), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("Internal Server Error!"), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @PostMapping(value = "/create-ship", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE
    }, produces =
            MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody ResponseEntity<?> createShipDetail(ShipDetailDTO shipDetailDTO,
                                                            HttpServletRequest request) {

        try {
            Integer userId = jwtService.getUserIdByToken(request);
            ResponseDTO responseDTO = shipDetailService.createShipDetail(userId, shipDetailDTO);
            if (responseDTO.getIsSuccess())
                return new ResponseEntity<>(new ResponseMessage(responseDTO.getMessage()), HttpStatus.CREATED);
            return new ResponseEntity<>(new ResponseMessage(responseDTO.getMessage()), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("Internal Server Error!"), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @PutMapping(value = "/update-ship", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE
    }, produces =
            MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateShipDetail(@RequestParam(value = "shipDetailId") Integer shipDetailId,
                                              ShipDetailDTO shipDetailDTO,
                                              HttpServletRequest request) {

        try {
            Integer userId = jwtService.getUserIdByToken(request);
            ResponseDTO responseDTO = shipDetailService.updateShipDetail(userId, shipDetailId, shipDetailDTO);
            if (responseDTO.getIsSuccess())
                return new ResponseEntity<>(new ResponseMessage(responseDTO.getMessage()), HttpStatus.OK);
            return new ResponseEntity<>(new ResponseMessage(responseDTO.getMessage()), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("Internal Server Error!"), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @DeleteMapping(value = "/delete-ship")
    public ResponseEntity<?> deleteShipDetail(@RequestParam(value = "shipDetailId") Integer shipDetailId,
                                              HttpServletRequest request) {

        try {
            Integer userId = jwtService.getUserIdByToken(request);
            ResponseDTO responseDTO = shipDetailService.deleteShipDetail(userId, shipDetailId);
            if (responseDTO.getIsSuccess())
                return new ResponseEntity<>(new ResponseMessage(responseDTO.getMessage()), HttpStatus.OK);
            return new ResponseEntity<>(new ResponseMessage(responseDTO.getMessage()), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("Internal Server Error!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
