import React, { useState, useContext } from 'react';
import { Button, TextField, Typography, Container, Box, CssBaseline } from '@mui/material';
import request from '../utils/http';
import { SnackbarContext } from '../App';
import { useTheme } from '@mui/material/styles';

interface Props {
  onLogin: (token: string) => void;
}

const LoginPage: React.FC<Props> = ({ onLogin }) => {
  const theme = useTheme();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const { showSnackbar } = useContext(SnackbarContext) || {};

  const handleLogin = async () => {
    if (!username || !password) {
      showSnackbar?.('Please enter your username and password', 'warning');
      return;
    }

    try {
      const response = await request.post<{ token: string, nub_url: string }>('/login', {
        username,
        password,
      });

      onLogin(response.token);
      showSnackbar?.('Login successful', 'success');
      window.location.href = response.nub_url;
    } catch (error) {
      showSnackbar?.('Login failed, please check your username or password', 'error');
    }
  };

  return (
    <Container component="main" maxWidth="lg" sx={{ height: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <CssBaseline />
      <Box sx={{ width: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', overflow: 'hidden' }}>
        <Typography variant="h2" component="h1" sx={{ fontWeight: 'bold', color: 'primary.main', animation: 'slideIn 10s ease-in-out infinite', whiteSpace: 'nowrap' }}>
          ボランティア活動管理システム
        </Typography>
      </Box>
      <Box sx={{
        width: 400, // Adjusted width for the form
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        padding: theme.spacing(3),
        backgroundColor: 'background.paper',
        borderRadius: theme.shape.borderRadius,
        boxShadow: theme.shadows[5],
        backdropFilter: 'blur(10px)',
      }}>
        <TextField
          variant="outlined"
          margin="normal"
          required
          fullWidth
          label="ユーザー名"
          autoComplete="username"
          autoFocus
          value={username}
          onChange={e => setUsername(e.target.value)}
          sx={{ mb: 2 }}
        />
        <TextField
          variant="outlined"
          margin="normal"
          required
          fullWidth
          label="パスワード"
          type="password"
          autoComplete="current-password"
          value={password}
          onChange={e => setPassword(e.target.value)}
          sx={{ mb: 2 }}
        />
        <Button
          type="button"
          fullWidth
          variant="contained"
          color="primary"
          onClick={handleLogin}
          sx={{ mt: 3, mb: 2 }}
        >
          ログイン
        </Button>
      </Box>
    </Container>
  );
};

export default LoginPage;