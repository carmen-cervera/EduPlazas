import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Home from './pages/Home'
import Login from './pages/auth/Login'
import RegistroEstudiante from './pages/auth/RegistroEstudiante'
import RegistroUniversidad from './pages/auth/RegistroUniversidad'
import EstudianteInicio from './pages/solicitudes/EstudianteInicio'
import CrearSolicitud from './pages/solicitudes/CrearSolicitud'
import ExplorarGrados from './pages/solicitudes/ExplorarGrados'
import VerSolicitud from './pages/solicitudes/VerSolicitud'


function ProtectedRoute({ element, allowedRoles }) {
  const usuarioGuardado = localStorage.getItem('usuario')
  const usuario = usuarioGuardado ? JSON.parse(usuarioGuardado) : null

  if (!usuario) {
    return <Navigate to="/estudiantes/login" replace />
  }

  if (!allowedRoles.includes(usuario.rol)) {
    if (usuario.rol === 'INVITADO') {
      return <Navigate to="/estudiante/grados" replace />
    }
    return <Navigate to="/" replace />
  }

  return element
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/estudiantes/login" element={<Login rol="ESTUDIANTE" />} />
        <Route path="/universidades/login" element={<Login rol="UNIVERSIDAD" />} />
        <Route path="/registro/estudiante" element={<RegistroEstudiante />} />
        <Route path="/registro/universidad" element={<RegistroUniversidad />} />
        <Route
          path="/estudiante/inicio"
          element={<ProtectedRoute element={<EstudianteInicio />} allowedRoles={['ESTUDIANTE', 'ADMIN']} />}
        />
        <Route
          path="/estudiante/solicitud"
          element={<ProtectedRoute element={<CrearSolicitud />} allowedRoles={['ESTUDIANTE', 'ADMIN']} />}
        />
        <Route
          path="/estudiante/grados"
          element={<ProtectedRoute element={<ExplorarGrados />} allowedRoles={['ESTUDIANTE', 'ADMIN', 'INVITADO']} />}
        />
        <Route
          path="/estudiante/ver-solicitud"
          element={<ProtectedRoute element={<VerSolicitud />} allowedRoles={['ESTUDIANTE', 'ADMIN']} />}
        />
      </Routes>
    </BrowserRouter>
  )
}

export default App