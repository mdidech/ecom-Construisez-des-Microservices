package com.ecommerce.micrommerce.web.controller;

import com.ecommerce.micrommerce.web.dao.ProductDao;
import com.ecommerce.micrommerce.web.exceptions.ProduitGratuitException;
import com.ecommerce.micrommerce.web.exceptions.ProduitIntrouvableException;
import com.ecommerce.micrommerce.web.model.Product;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

@Api( "API pour les opérations CRUD sur les produits.")
@RestController
public class ProductController {

    private final ProductDao productDao;

    public ProductController(ProductDao productDao) {
        this.productDao = productDao;
    }

    @DeleteMapping (value = "/produits/{id}")
    public void supprimerProduit(@PathVariable int id) {
        productDao.deleteById(id);
    }

    @PutMapping (value = "/produits")
    public void updateProduit(@RequestBody Product product) {
        productDao.save(product);
    }

    //Récupérer la liste des produits
    @GetMapping("/produits")
    public List<Product> listeProduits() {
        return productDao.findAll();
    }

    @ApiOperation(value = "Récupère un produit grâce à son ID à condition que celui-ci soit en stock!")
    @GetMapping(value = "/produits/{id}")
    public Product afficherUnProduit(@PathVariable int id) {
        Product produit = productDao.findById(id);
        if(produit==null) throw new ProduitIntrouvableException("Le produit avec l'id " + id + " est INTROUVABLE. Écran Bleu si je pouvais.");
        return produit;
    }

    @GetMapping(value = "test/produits/{prixLimit}")
    public List<Product> testeDeRequetes(@PathVariable int prixLimit) {
        return productDao.findByPrixGreaterThan(prixLimit);
    }

    @GetMapping("/adminproduits")
    public HashMap<String,Integer> calculerMargeProduit(){
        List<Product> produits=productDao.findAll();
        HashMap<String,Integer> marges=new HashMap<>();
        produits.forEach(produit->{
            marges.put(produit.toString(),produit.getPrix()- produit.getPrixAchat());
        });

        return marges;
    }

    @GetMapping("/triproduits")
    public List<Product> trierProduitsParOrdreAlphabetique(){
        return productDao.findAllByOrderByNom();
    }

    @PostMapping("/produits")
    public Product ajouterProduit(@Valid @RequestBody Product product){
        System.out.println(product.toString());
        if (product.getPrix()<=0) throw  new ProduitGratuitException("Le Prix de vente doit etre superieru à 0");
        return productDao.save(product);
    }
}