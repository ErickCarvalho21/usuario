package com.javanauta.usuario.business;

import com.javanauta.usuario.business.converter.UsuarioConverter;
import com.javanauta.usuario.business.dto.EnderecoDTO;
import com.javanauta.usuario.business.dto.TelefoneDTO;
import com.javanauta.usuario.business.dto.UsuarioDTO;
import com.javanauta.usuario.infrastructure.entity.Endereco;
import com.javanauta.usuario.infrastructure.entity.Telefone;
import com.javanauta.usuario.infrastructure.entity.Usuario;
import com.javanauta.usuario.infrastructure.exceptions.ConflictException;
import com.javanauta.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.javanauta.usuario.infrastructure.repository.EnderecoRepository;
import com.javanauta.usuario.infrastructure.repository.TelefoneRepository;
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
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;

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

    public UsuarioDTO buscarUsuarioPorEmail(String email) {
        try {
            return usuarioConverter.paraUsuarioDTO(
                    usuarioRepository.findByEmail(email)
                            .orElseThrow(() -> new ResourceNotFoundException
                    ("Email nao encontrado" + email)
                            )
            );
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Email naoo encontrado" + email);
        }
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
    public EnderecoDTO atualizaEndereco(Long idEndereco, EnderecoDTO enderecoDTO){
        Endereco entity = enderecoRepository.findById(idEndereco).orElseThrow(() ->
                new ResourceNotFoundException("ID nao encontrado" + idEndereco));

        Endereco endereco = usuarioConverter.upadateEndereco(enderecoDTO,entity);

        enderecoRepository.save(endereco);

        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));
    }

    public TelefoneDTO atualizaTelefone(Long idTelefone, TelefoneDTO dto){
        Telefone entity = telefoneRepository.findById(idTelefone).orElseThrow(() ->
                new ResourceNotFoundException("Id nao encontrado" + idTelefone));
        Telefone telefone = usuarioConverter.upadateTelefone(dto,entity);

        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));

    }
    public EnderecoDTO cadrastaEndereco(String token, EnderecoDTO dto){
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email nao localizado" + email));

        Endereco endereco = usuarioConverter.paraEnderecoEntity(dto, usuario.getId());
        Endereco enderecoEntity = enderecoRepository.save(endereco);
        return usuarioConverter.paraEnderecoDTO(enderecoEntity);
    }

    public TelefoneDTO cadrastaTelefone(String token, TelefoneDTO dto){
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuario= usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Telefone nao localizado" + email));

        Telefone telefone = usuarioConverter.paraTelefoneEntity(dto, usuario.getId());
        Telefone telefoneEtity = telefoneRepository.save(telefone);
        return usuarioConverter.paraTelefoneDTO(telefoneEtity);
    }



}
