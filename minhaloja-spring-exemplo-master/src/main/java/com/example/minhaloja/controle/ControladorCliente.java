package com.example.minhaloja.controle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.validation.Valid;

import com.example.minhaloja.modelo.Cliente;
import com.example.minhaloja.repositorios.RepositorioCliente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class ControladorCliente {

    @Autowired
    RepositorioCliente repositorioCliente;

    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView retorno = new ModelAndView("index.html");
        return retorno;
    }

    @RequestMapping("/formulario_cliente")
    public ModelAndView formularioCliente(Cliente cliente) {
        ModelAndView retorno = new ModelAndView("cadastroCliente.html");
        return retorno;
    }

    // @InitBinder
    // protected void initbinder(WebDataBinder binder){
    // binder.setValidator();
    // }

    @RequestMapping("/novo_cliente")
    public ModelAndView cadastroCliente(@Valid Cliente cliente, BindingResult bidingResult, RedirectAttributes redirect,
            @RequestParam(value = "foto", required = false) MultipartFile foto) {
        ModelAndView retorno;
        if (bidingResult.hasErrors()) {
            redirect.addFlashAttribute("cliente", cliente);
            retorno = new ModelAndView("cadastroCliente.html");
            return retorno;
        }

        retorno = new ModelAndView("redirect:/");        
        repositorioCliente.save(cliente);

        if (!foto.isEmpty()) {
            String path = processaArquivo(cliente, foto);
            cliente.setPathToFoto(path);
            repositorioCliente.save(cliente);
        } 

        redirect.addFlashAttribute("mensagem", "Cliente cadastrado com sucesso!");
        return retorno;
    }

    private String processaArquivo(Cliente cliente, MultipartFile foto) {
        try {
            byte[] conteudo = foto.getBytes();            
            Path path = Paths.get("upload-dir" + File.separator + cliente.getId());
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
    

}