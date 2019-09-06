package com.example.minhaloja.controle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.example.minhaloja.modelo.Item;
import com.example.minhaloja.repositorios.RepositorioItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class ControladorItem{
    
    @Autowired
    RepositorioItem repositorioItem;

    @RequestMapping("/formulario_item")
    public ModelAndView formularioItem(){
        ModelAndView retorno = new ModelAndView("cadastroItens.html");
        return retorno;
    }

    @RequestMapping("/novo_item")
    public ModelAndView novoItem(Item item, @RequestParam(value = "foto", required = false) MultipartFile foto){
        repositorioItem.save(item);
        ModelAndView retorno = new ModelAndView("index.html");

        if (!foto.isEmpty()) {
            String path = processaArquivo(item, foto);
            item.setCaminhoFoto(path);
            repositorioItem.save(item);
        } 
        return retorno;
    }

    private String processaArquivo(Item item, MultipartFile foto) {
        try {
            byte[] conteudo = foto.getBytes();            
            Path path = Paths.get("src/resources/images" + File.separator + item.getId());
            if(!Files.exists(path)){
                Files.createDirectories(path);
            }

            Files.write(path.resolve(foto.getOriginalFilename()), conteudo);
            return path.resolve(foto.getOriginalFilename()).toString();
            // FileUtils.writeByteArrayToFile(new File("ondevaificar"), conteudo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @RequestMapping("/lista_item")
    public ModelAndView listaItens(){
        ModelAndView retorno = new ModelAndView("listaItens.html");
        Iterable<Item> itens = repositorioItem.findAll();
        // List<Item> itens2 = (List<Item>)itens;
        // System.out.println("Caminho : "+itens2.get(0).getCaminhoFoto());
        retorno.addObject("itens", itens);

        return retorno;
    }
}