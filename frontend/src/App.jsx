import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Home from './pages/Home'
import Login from './pages/auth/Login'
import RegistroEstudiante from './pages/auth/RegistroEstudiante'
import RegistroUniversidad from './pages/auth/RegistroUniversidad'
import EstudianteInicio from './pages/solicitudes/EstudianteInicio'
import CrearSolicitud from './pages/solicitudes/CrearSolicitud'
import ExplorarGrados from './pages/solicitudes/ExplorarGrados'
import VerSolicitud from './pages/solicitudes/VerSolicitud'


function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/estudiantes/login" element={<Login rol="ESTUDIANTE" />} />
        <Route path="/universidades/login" element={<Login rol="UNIVERSIDAD" />} />
        <Route path="/registro/estudiante" element={<RegistroEstudiante />} />
        <Route path="/registro/universidad" element={<RegistroUniversidad />} />
        <Route path="/estudiante/inicio" element={<EstudianteInicio />} />
        <Route path="/estudiante/solicitud" element={<CrearSolicitud />} />
        <Route path="/estudiante/grados" element={<ExplorarGrados />} />
        <Route path="/estudiante/ver-solicitud" element={<VerSolicitud />} />     
      </Routes>
    </BrowserRouter>
  )
}

export default App