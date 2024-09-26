/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import model.Usuario;
import utils.Utils;

/**
 *
 * @author s.lucas
 */
public class UsuarioController {

    public boolean autenticar(String email, String senha) {
        String sql = "SELECT * from TBUSUARIO "
                + " WHERE email = ? and senha = ?"
                + " and ativo = true";

        GerenciadorConexao gerenciador = new GerenciadorConexao();
        PreparedStatement comando = null;
        ResultSet resultado = null;

        try {
            comando = gerenciador.prepararComando(sql);

            comando.setString(1, email);
            comando.setString(2, senha);

            resultado = comando.executeQuery();

            if (resultado.next()) {
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            gerenciador.fecharConexao(comando, resultado);
        }
        return false;
    }

    public boolean inserirUsuario(Usuario usu) {
        String sql = "INSERT into TBUSUARIO (nome, email, senha, datanasc, ativo, imagem) "
                + "VALUES (?,?,?,?,?,?)";

        GerenciadorConexao gerenciador = new GerenciadorConexao();
        PreparedStatement comando = null;
        byte[] iconBytes = null;

        try {
            if (usu.getImagem() != null) {
                iconBytes = Utils.converterIconToBytes(usu.getImagem());
            }

            comando = gerenciador.prepararComando(sql);
            comando.setString(1, usu.getNome());
            comando.setString(2, usu.getEmail());
            comando.setString(3, usu.getSenha());
            comando.setDate(4, new java.sql.Date(usu.getDataNasc().getTime()));
            comando.setBoolean(5, usu.isAtivo());
            comando.setBytes(6, iconBytes);

            comando.executeUpdate();

            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ERRO: " + e.getMessage());
        } finally {
            gerenciador.fecharConexao(comando);
        }

        return false;
    }

    public boolean alterarUsuario(Usuario usuario) {
        String sql = "UPDATE tbusuario SET nome = ?, email = ?, senha = ?, "
                + " datanasc = ?, ativo = ?, imagem = ? WHERE "
                + " pkusuario = ?";

        GerenciadorConexao gerenciador = new GerenciadorConexao();
        PreparedStatement comando = null;

        try {
            byte[] iconBytes = Utils.converterIconToBytes(usuario.getImagem());

            comando = gerenciador.prepararComando(sql);
            comando.setString(1, usuario.getNome());
            comando.setString(2, usuario.getEmail());
            comando.setString(3, usuario.getSenha());
            comando.setDate(4, new java.sql.Date(usuario.getDataNasc().getTime()));
            comando.setBoolean(5, usuario.isAtivo());
            comando.setBytes(6, iconBytes);
            comando.setInt(7, usuario.getPkUsuario());

            comando.executeUpdate();

            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ERRO NA ALTERAÇÃO DE USÚARIO.");
        } finally {
            gerenciador.fecharConexao(comando);
        }

        return false;
    }

    public List<Usuario> buscarUsuarios(int tipoFiltro, String filtro) {
        String sql = "SELECT * FROM dbprojeto.tbusuario";

        if (!filtro.equals("")) {
            if (tipoFiltro == 0 || tipoFiltro == 1) {
                sql = sql + " WHERE NOME LIKE ?";
            } else {
                sql = sql + " WHERE EMAIL LIKE ?";
            }
        }

        GerenciadorConexao gerenciador = new GerenciadorConexao();
        PreparedStatement comando = null;
        ResultSet resultado = null;

        List<Usuario> listaUsuarios = new ArrayList<>();

        try {
            comando = gerenciador.prepararComando(sql);

            if (!filtro.equals("")) {
                if (tipoFiltro == 0) {
                    comando.setString(1, filtro + "%");
                } else if (tipoFiltro == 1) {
                    comando.setString(1, "%" + filtro + "%");
                } else if (tipoFiltro == 2) {
                    comando.setString(1, filtro + "%");
                } else {
                    comando.setString(1, "%" + filtro + "%");
                }
            }

            resultado = comando.executeQuery();

            while (resultado.next()) {

                Usuario usu = new Usuario();

                usu.setPkUsuario(resultado.getInt("pkusuario"));
                usu.setNome(resultado.getString("nome"));
                usu.setEmail(resultado.getString("email"));
                usu.setSenha(resultado.getString("senha"));
                usu.setDataNasc(resultado.getDate("dataNasc"));
                usu.setAtivo(resultado.getBoolean("ativo"));

                listaUsuarios.add(usu);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            gerenciador.fecharConexao(comando, resultado);
        }
        return listaUsuarios;
    }

    public boolean excluir(int pkUsuario) {

        String sql = "DELETE FROM tbusuario WHERE pkusuario = ?";

        GerenciadorConexao gerenciador = new GerenciadorConexao();
        PreparedStatement comando = null;

        try {
            comando = gerenciador.prepararComando(sql);
            comando.setInt(1, pkUsuario);

            comando.executeUpdate();

            return true;

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir: " + ex);
        } finally {
            gerenciador.fecharConexao(comando);
        }

        return false;
    }

    public Usuario buscarPorPk(int pkUsuario) {

        //Guarda o sql
        String sql = "SELECT * FROM tbusuario WHERE pkusuario = ? ";

        //cria um gerenciador de conexao
        GerenciadorConexao gerenciador = new GerenciadorConexao();
        PreparedStatement comando = null;
        ResultSet resultado = null;

        Usuario usu = new Usuario();

        try {
            comando = gerenciador.prepararComando(sql);

            comando.setInt(1, pkUsuario);

            resultado = comando.executeQuery();

            if (resultado.next()) {

                usu.setPkUsuario(resultado.getInt("pkusuario"));
                usu.setNome(resultado.getString("nome"));
                usu.setEmail(resultado.getString("email"));
                usu.setSenha(resultado.getString("senha"));
                usu.setDataNasc(resultado.getDate("dataNasc"));
                usu.setAtivo(resultado.getBoolean("ativo"));

                byte[] bytes = resultado.getBytes("imagem");
                if (bytes != null) {
                    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                    BufferedImage imagem = ImageIO.read(bis);

                    usu.setImagem(new ImageIcon(imagem));
                }
            }
        } catch (SQLException | IOException ex) {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            gerenciador.fecharConexao(comando, resultado);
        }
        return usu;

    }

}
