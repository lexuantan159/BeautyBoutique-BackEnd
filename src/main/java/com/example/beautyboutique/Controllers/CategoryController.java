package com.example.beautyboutique.Controllers;

import com.example.beautyboutique.Models.Category;
import com.example.beautyboutique.Models.Product;
import com.example.beautyboutique.Payload.Response.ResponseMessage;
import com.example.beautyboutique.Services.Category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/category")
public class CategoryController {
     @Autowired
     private CategoryService categoryService;

     @GetMapping("/get-all") //http://localhost:8080/api/category/get-all
     public ResponseEntity<List<Category>> getAllCategory() {
          List<Category> categories = categoryService.findAll();
          if (categories.isEmpty()) {
               return new ResponseEntity<>(HttpStatus.NOT_FOUND);
          } else {
               return new ResponseEntity<>(categories, HttpStatus.OK);
          }
     }
     @GetMapping("/findById/{id}")//http://localhost:8080/api/product/findById
     public ResponseEntity<Category> findById(@PathVariable Integer id) {
          System.out.println("Find by id");
          Category categories = categoryService.findById(id);

          if (categories != null) {
               System.out.println(categories.getId());
               return new ResponseEntity<>(categories, HttpStatus.OK);
          } else {
               return new ResponseEntity<>(HttpStatus.NOT_FOUND);
          }
     }
     @PostMapping("/create")
     public ResponseEntity<ResponseMessage> createCategory(@RequestBody Category categories ) {
          System.out.println("Create new product");
          Category categoryCreat = categoryService.saveafftercheck(categories);
          if(categoryCreat == null) {
               return new ResponseEntity<>(new ResponseMessage("Add category fail."), HttpStatus.BAD_REQUEST);
          }
          else {
               return new ResponseEntity<>(new ResponseMessage("Add category success !!!"), HttpStatus.OK);
          }
     }
     @GetMapping("/search")
     public ResponseEntity<ResponseMessage> searchCategory(@RequestParam String categoryName) {
          if (categoryName == null || categoryName.trim().isEmpty()) {
               return new ResponseEntity<>(new ResponseMessage("Product name is required"), HttpStatus.BAD_REQUEST);
          }
          List<Category> categories = categoryService.findByName(categoryName);
          if (categories.isEmpty()) {
               return new ResponseEntity<>(new ResponseMessage("Product not found"), HttpStatus.NOT_FOUND);
          } else {
               return ResponseEntity.ok((ResponseMessage) categories);
          }
     }
     @DeleteMapping("/delete/{id}")
     public ResponseEntity<Category> delete(@PathVariable Integer id) {
          Category deletedCategory = categoryService.delete(id);
          return new ResponseEntity<>(deletedCategory, HttpStatus.NO_CONTENT);
     }
     @GetMapping("/get/{id}")
     public  ResponseEntity<Category> get(@PathVariable Integer id) {
          Category getCategory = categoryService.get(id);
          return  new ResponseEntity<>(getCategory,HttpStatus.OK );
     }

}
