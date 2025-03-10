import React, { useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import HomePage from './pages/HomePage';
import OAuth2Callback from './pages/OAuth2Callback';

import { Snackbar, Alert } from '@mui/material';
import './App.css';

export const SnackbarContext = React.createContext<{ showSnackbar: (msg: string, type?: 'success' | 'error' | 'warning' | 'info') => void } | null>(null);

const App: React.FC = () => {
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(() => !!localStorage.getItem('token'));

  const handleLogin = (token: string) => {
    localStorage.setItem('token', token);
    setIsLoggedIn(true);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    setIsLoggedIn(false);
  };

  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');
  const [snackbarSeverity, setSnackbarSeverity] = useState<'success' | 'error' | 'warning' | 'info'>('info');

  const showSnackbar = (msg: string, type: 'success' | 'error' | 'warning' | 'info' = 'info') => {
    setSnackbarMessage(msg);
    setSnackbarSeverity(type);
    setSnackbarOpen(true);
  };

  return (
    <SnackbarContext.Provider value={{ showSnackbar }}>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<OAuth2Callback />} />
          <Route path="/login" element={!isLoggedIn ? <LoginPage onLogin={handleLogin} /> : <Navigate to="/home" />} />
          <Route path="/home/*" element={<HomePage onLogout={handleLogout} />} />
        </Routes>
      </BrowserRouter>

      <Snackbar open={snackbarOpen} autoHideDuration={3000} onClose={() => setSnackbarOpen(false)}>
        <Alert severity={snackbarSeverity} onClose={() => setSnackbarOpen(false)}>
          {snackbarMessage}
        </Alert>
      </Snackbar>
    </SnackbarContext.Provider>
  );
};

export default App;
