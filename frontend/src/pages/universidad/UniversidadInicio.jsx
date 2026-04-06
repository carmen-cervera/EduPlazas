import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './UniversidadInicio.module.css';
import logo from '../../assets/LogoPequeño_FondoBlanco_SinGorro.png';
import { obtenerUniversidades } from '../../services/authService';

const UniversidadInicio = () => {
  const navigate = useNavigate();
  const [grados, setGrados] = useState([]);
  const [nombreUni, setNombreUni] = useState('');
  useEffect(() => {
    const ofertasGuardadas = JSON.parse(localStorage.getItem('misOfertas')) || [];
    setGrados(ofertasGuardadas);

    const dataUsuario = localStorage.getItem('usuario');
    if (dataUsuario) {
      const usuarioObj = JSON.parse(dataUsuario);
      obtenerUniversidades()
        .then(res => {
          const lista = res.data;
          const miUni = lista.find(u => u.id === usuarioObj.id);
          if (miUni) {
            setNombreUni(miUni.nombre);
          } else {
            setNombreUni(usuarioObj.email);
          }
        })
        .catch(err => {
          console.error("Error al cargar universidades:", err);
          setNombreUni(usuarioObj.email);
        });
    }
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
    navigate('/universidades/login');
  };

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <img src={logo} alt="Logo" className={styles.logoImg} />
        <h1 className={styles.tituloHeader}>Bienvenid@</h1>
      </header>

      <div className={styles.content}>
        <aside className={styles.sidebar}>
          <div className={styles.uLogoSection}>
            <div className={styles.avatar}>🏛️</div>
            <p className={styles.uNombre}>{nombreUni}</p>
          </div>

          <nav className={styles.navLinks}>
            <button
              className={styles.sidebarButton}
              onClick={() => navigate('/universidad/publicar')}
            >
              Publicar oferta
            </button>
            <button className={styles.sidebarButton}>Panel de demanda</button>
          </nav>

          <button className={styles.logoutBtn} onClick={handleLogout}>Log out</button>
        </aside>

        <main className={styles.main}>
          {/* Banner superior */}
          <div className={styles.banner}>
            <p className={styles.bannerText}>Convocatoria abierta:</p>
            <h3 className={styles.bannerTitle}>EvAU junio 2026</h3>
          </div>

          {/* Contenedor de la tabla */}
          <div className={styles.tableCard}>
            <h2 className={styles.tableTitle}>Grados publicados</h2>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Nombre</th>
                  <th>Comunidad Autónoma</th>
                  <th>Rama</th>
                  <th>Plazas</th>
                  <th style={{ width: '250px' }}>Ponderaciones (0.2)</th>
                  <th style={{ width: '250px' }}>Ponderaciones (0.1)</th>
                </tr>
              </thead>
              <tbody>
                { }
                {grados.map((grado, index) => (
                  <tr key={index}>
                    <td>{grado.nombre}</td>
                    <td>{grado.comunidad}</td>
                    <td>{grado.rama}</td>
                    <td>{grado.plazas}</td>
                    <td>
                      {grado.ponderaciones02 && grado.ponderaciones02.length > 0
                        ? grado.ponderaciones02.join(', ')
                        : "---"}
                    </td>
                    <td>
                      {grado.ponderaciones01 && grado.ponderaciones01.length > 0
                        ? grado.ponderaciones02.join(', ')
                        : "---"}
                    </td>

                  </tr>
                ))}

                { }
                <tr>
                  <td>Ingeniería Informática</td>
                  <td>Comunidad de Madrid</td>
                  <td>Ingeniería y Arquitectura</td>
                  <td>150</td>
                  <td>Matemáticas II, Tecnología e Ingeniería II</td>
                  <td>Dibujo Técnico II, Matemáticas Apl. CC. Soc. II</td>
                </tr>
                <tr>
                  <td>Arquitectura</td>
                  <td>Comunidad de Madrid</td>
                  <td>Ingeniería y Arquitectura</td>
                  <td>120</td>
                  <td>Dibujo Técnico II, Matemáticas II</td>
                  <td>Matemáticas Apl. CC. Soc. II, Tecnología e Ingeniería II</td>
                </tr>


              </tbody>
            </table>
          </div>
        </main>
      </div>
    </div>
  );
};

export default UniversidadInicio;
