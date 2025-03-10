import React, { useEffect, useState, useContext } from 'react';
import {
  Container, Typography, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Paper, Button, Dialog, DialogActions, DialogContent,
  DialogTitle, TextField, Select, MenuItem
} from '@mui/material';
import request from '../utils/http';
import { SnackbarContext } from '../App';

interface User {
  id: number;
  username: string;
  role: number;
  status: number;
}

const UserManagement: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [openDialog, setOpenDialog] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState(2);
  const [status, setStatus] = useState(1);
  const { showSnackbar } = useContext(SnackbarContext) || {};

  const fetchUsers = async () => {
    try {
      const data = await request.get<User[]>('/user');
      setUsers(data);
    } catch (error) {
      showSnackbar?.('Failed to obtain user list', 'error');
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleOpenDialog = (user: User | null = null) => {
    setEditingUser(user);
    setUsername(user ? user.username : '');
    setPassword('');
    setRole(user ? user.role : 2);
    setStatus(user ? user.status : 1);
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setEditingUser(null);
    setUsername('');
    setPassword('');
    setRole(2);
    setStatus(1);
  };

  const handleSubmit = async () => {
    if (!username.trim()) {
      showSnackbar?.('ユーザー名を入力してください', 'warning');
      return;
    }

    try {
      if (editingUser) {
        await request.put(`/user/${editingUser.id}`, {
          username,
          password: password || undefined,
          role,
          status
        });
        showSnackbar?.('ユーザーが正常に更新されました', 'success');
      } else {
        await request.post('/user', { username, password, role });
        showSnackbar?.('ユーザーが正常に作成されました', 'success');
      }
      fetchUsers();
      handleCloseDialog();
    } catch (error) {
      showSnackbar?.('Operation failed', 'error');
    }
  };

  return (
    <Container>
      <Typography variant="h4" gutterBottom>
        ユーザー管理
      </Typography>
      <Button variant="contained" color="primary" onClick={() => handleOpenDialog(null)} sx={{ mb: 2 }}>
        ユーザーを追加
      </Button>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>ユーザー名</TableCell>
              <TableCell>権限</TableCell>
              <TableCell>状態</TableCell>
              <TableCell align="right">操作</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {users.map((user) => (
              <TableRow key={user.id}>
                <TableCell>{user.id}</TableCell>
                <TableCell>{user.username}</TableCell>
                <TableCell>{user.role === 1 ? '管理者' : 'オペレーター'}</TableCell>
                <TableCell>{user.status === 1 ? '有効' : '無効'}</TableCell>
                <TableCell align="right">
                  <Button variant="outlined" color="primary" onClick={() => handleOpenDialog(user)} sx={{ mr: 1 }}>
                    編集
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={openDialog} onClose={handleCloseDialog}>
        <DialogTitle>{editingUser ? 'ユーザーを編集' : 'ユーザーを追加'}</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label="ユーザー名"
            variant="outlined"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            sx={{ mt: 2 }}
          />
          <TextField
            fullWidth
            label="パスワード"
            type="password"
            variant="outlined"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            sx={{ mt: 2 }}
          />
          <Select
            fullWidth
            value={role}
            onChange={(e) => setRole(Number(e.target.value))}
            sx={{ mt: 2 }}
          >
            <MenuItem value={1}>管理者</MenuItem>
            <MenuItem value={2}>オペレーター</MenuItem>
          </Select>
          <Select
            fullWidth
            value={status}
            onChange={(e) => setStatus(Number(e.target.value))}
            sx={{ mt: 2 }}
          >
            <MenuItem value={1}>有効</MenuItem>
            <MenuItem value={0}>無効</MenuItem>
          </Select>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog} color="secondary">キャンセル</Button>
          <Button onClick={handleSubmit} color="primary" variant="contained">
            {editingUser ? '更新' : '追加'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default UserManagement;
