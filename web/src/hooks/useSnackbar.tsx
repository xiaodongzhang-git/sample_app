import { useState } from 'react';
import { Snackbar, Alert } from '@mui/material';

export const useSnackbar = () => {
  const [open, setOpen] = useState(false);
  const [message, setMessage] = useState('');
  const [severity, setSeverity] = useState<'success' | 'error' | 'warning' | 'info'>('info');

  const showSnackbar = (msg: string, type: 'success' | 'error' | 'warning' | 'info' = 'info') => {
    setMessage(msg);
    setSeverity(type);
    setOpen(true);
  };

  const SnackbarComponent = (
    <Snackbar open={open} autoHideDuration={3000} onClose={() => setOpen(false)}>
      <Alert severity={severity} onClose={() => setOpen(false)}>
        {message}
      </Alert>
    </Snackbar>
  );

  return { showSnackbar, SnackbarComponent };
};
