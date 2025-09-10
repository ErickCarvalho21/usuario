package com.javanauta.usuario.business;

import com.javanauta.usuario.business.converter.UsuarioConverter;
import com.javanauta.usuario.business.dto.UsuarioDTO;
import com.javanauta.usuario.infrastructure.entity.Usuario;
import com.javanauta.usuario.infrastructure.exceptions.ConflictException;
import com.javanauta.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.javanauta.usuario.infrastructure.repository.UsuarioRepository;
import com.javanauta.usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoderr;
    private final JwtUtil jwtUtil;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoderr

                .encode(usuarioDTO.getSenha()));

        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
         return usuarioConverter.paraUsuarioDTO(
                 usuarioRepository.save(usuario));
    }
    public void emailExiste(String email){
        try{
            boolean existe = vereficaEmailExistente(email);
            if(existe){
                throw new ConflictException("Emaill ja cadrastado" + email);
            }
        }catch (ConflictException e){
            throw new ConflictException("Emaill ja cadrastado " + e.getCause());
        }
    }
    public boolean vereficaEmailExistente(String email){
        return usuarioRepository.existsByEmail(email);
    }

    public Usuario buscarUsuarioPorEmail(String email){
        return usuarioRepository.findByEmail(email).orElseThrow(()   -> new ResourceNotFoundException
                ("Email nao encontrado" + email));
    }
    public void deletaUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);

    }

    public UsuarioDTO atualizaDadosUsuario(String token, UsuarioDTO dto) {
        //Aqui Busacamos o email do usuarion atraves do token pra tirar a obrigatoriedade de passar email//
        String email = jwtUtil.extrairEmailToken(token.substring(7));

        //Criptografias de senha
        dto.setSenha(dto.getSenha() != null ? passwordEncoderr.encode(dto.getSenha()) : null);


        //Busca os dados do usuario no banco de dados
        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(() ->

                //Mesclou os dados que recebemos na requisiçao dt com os dados do bancos de dados
                new ResourceNotFoundException("Email nao localizado"));

        //Colocou criptografia na nossa senha
        Usuario usuario = usuarioConverter.updateUsuario(dto, usuarioEntity);

        //Salvou os dasos dos usuario convertido e depois pegou oo retorno e converteu para usuarioDTo
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));





    }

}
