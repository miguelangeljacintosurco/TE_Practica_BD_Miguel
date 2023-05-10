/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.emergentes.controlador;

import com.emergentes.modelo.Libro;
import com.emergentes.utiles.ConexionDB;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Miguel
 */
@WebServlet(name = "MainController", urlPatterns = {"/MainController"})
public class MainController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String op;
            op = (request.getParameter("op") != null) ? request.getParameter("op") : "list";
            ArrayList<Libro> lista = new ArrayList<Libro>();
            ConexionDB canal = new ConexionDB();
            Connection conn = canal.conectar();
            PreparedStatement ps;
            ResultSet rs;
            if (op.equals("list")) {

                //para listar los datos
                String sql = "SELECT * FROM libros";
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();
                while (rs.next()) {
                    Libro lib = new Libro();
                    lib.setId(rs.getInt("id"));
                    lib.setIsbn(rs.getString("isbn"));
                    lib.setTitulo(rs.getString("titulo"));
                    lib.setCategoria(rs.getString("categoria"));
                    lista.add(lib);
                }
                request.setAttribute("lista", lista);
                //enviar al index mostrar la informacion
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
            if (op.equals("nuevo")) {
                //nuevo registro
                Libro li = new Libro();
                request.setAttribute("lib", li);
                request.getRequestDispatcher("editar.jsp").forward(request, response);
            }
            if (op.equals("eliminar")) {
                //obtener el id
                int id = Integer.parseInt(request.getParameter("id"));
                ///realiza la eliminacion de la base de datos
                String sql = "DELETE FROM libros WHERE id= ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, id);
                ps.executeUpdate();
                //redirecciona a maincontroler
                response.sendRedirect("MainController");
            } 
   if (op.equals("editar")) {
    //editar registro
    int id = Integer.parseInt(request.getParameter("id"));
    String sql = "SELECT * FROM libros WHERE id= ?";
    ps = conn.prepareStatement(sql);
    ps.setInt(1, id);
    rs = ps.executeQuery();
    Libro lib = new Libro();
    if (rs.next()) {
        lib.setId(rs.getInt("id"));
        lib.setIsbn(rs.getString("isbn"));
        lib.setTitulo(rs.getString("titulo"));
        lib.setCategoria(rs.getString("categoria"));
    }
    request.setAttribute("lib", lib);
    request.getRequestDispatcher("editar.jsp").forward(request, response);
}
            
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String isbn = request.getParameter("isbn");
            String titulo = request.getParameter("titulo");
            String categoria = request.getParameter("categoria");
            Libro lib = new Libro();
            lib.setId(id);
            lib.setIsbn(isbn);
            lib.setTitulo(titulo);
            lib.setCategoria(categoria);

            ConexionDB canal = new ConexionDB();
            Connection con = canal.conectar();
            PreparedStatement ps;
            if (id == 0) {
                //nuevo registro
                String sql = "INSERT INTO libros (isbn,titulo,categoria) VALUES (?,?,?)";
                ps = con.prepareStatement(sql);
                ps.setString(1, lib.getIsbn());
                ps.setString(2, lib.getTitulo());
                ps.setString(3, lib.getCategoria());
                ps.executeUpdate();
            }else {
            //editar registro
            String sql = "UPDATE libros SET isbn=?, titulo=?, categoria=? WHERE id=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, lib.getIsbn());
            ps.setString(2, lib.getTitulo());
            ps.setString(3, lib.getCategoria());
            ps.setInt(4, lib.getId());
            ps.executeUpdate();
        }
        response.sendRedirect("MainController");
    } catch (SQLException e) {
        System.out.println("Error en SQL "+ e.getMessage());
    }
}
    }        
        
