import React, { useEffect, useState, useContext } from 'react';
import {
  Container, Typography, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Paper, Button, Dialog, DialogActions, DialogContent,
  DialogTitle, TextField, Select, MenuItem
} from '@mui/material';
import request from '../utils/http';
import { SnackbarContext } from '../App';

interface Volunteer {
  id: number;
  name: string;
  email: string;
  phoneNumber: string;
  gender: number;
  status: number;
}

const VolunteerManagement: React.FC = () => {
  const [volunteers, setVolunteers] = useState<Volunteer[]>([]);
  const [openDialog, setOpenDialog] = useState(false);
  const [editingVolunteer, setEditingVolunteer] = useState<Volunteer | null>(null);
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [phoneNumber, setphoneNumber] = useState('');
  const [gender, setGender] = useState(1);
  const [status, setStatus] = useState(1);
  const { showSnackbar } = useContext(SnackbarContext) || {};

  const fetchVolunteers = async () => {
    try {
      const data = await request.get<Volunteer[]>('/volunteer');
      setVolunteers(data);
    } catch (error) {
      showSnackbar?.('Failed to obtain volunteer list', 'error');
    }
  };

  useEffect(() => {
    fetchVolunteers();
  }, []);

  const handleOpenDialog = (volunteer: Volunteer | null = null) => {
    setEditingVolunteer(volunteer);
    setName(volunteer ? volunteer.name : '');
    setEmail(volunteer ? volunteer.email : '');
    setphoneNumber(volunteer ? volunteer.phoneNumber : '');
    setGender(volunteer ? volunteer.gender : 1);
    setStatus(volunteer ? volunteer.status : 1);
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setEditingVolunteer(null);
    setName('');
    setEmail('');
    setphoneNumber('');
    setGender(1);
    setStatus(1);
  };

  const handleSubmit = async () => {
    if (!name.trim() || !email.trim() || !phoneNumber.trim()) {
      showSnackbar?.('Please fill in all fields', 'warning');
      return;
    }

    const volunteerData = {
      name,
      email,
      phoneNumber,
      gender,
      status
    };

    try {
      if (editingVolunteer) {
        await request.put(`/volunteer/${editingVolunteer.id}`, volunteerData);
        showSnackbar?.('Volunteer successfully updated', 'success');
      } else {
        await request.post('/volunteer', volunteerData);
        showSnackbar?.('Volunteer successfully added', 'success');
      }
      fetchVolunteers();
      handleCloseDialog();
    } catch (error) {
      showSnackbar?.('Operation failed', 'error');
    }
  };

  return (
    <Container>
      <Typography variant="h4" gutterBottom>
        ボランティア管理
      </Typography>
      <Button variant="contained" color="primary" onClick={() => handleOpenDialog(null)} sx={{ mb: 2 }}>
        ボランティアを追加
      </Button>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>名前</TableCell>
              <TableCell>メール</TableCell>
              <TableCell>電話番号</TableCell>
              <TableCell>性別</TableCell>
              <TableCell>状態</TableCell>
              <TableCell align="right">操作</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {volunteers.map((volunteer) => (
              <TableRow key={volunteer.id}>
                <TableCell>{volunteer.id}</TableCell>
                <TableCell>{volunteer.name}</TableCell>
                <TableCell>{volunteer.email}</TableCell>
                <TableCell>{volunteer.phoneNumber}</TableCell>
                <TableCell>{volunteer.gender === 1 ? '男' : '女'}</TableCell>
                <TableCell>{volunteer.status === 1 ? '有効' : '無効'}</TableCell>
                <TableCell align="right">
                  <Button variant="outlined" color="primary" onClick={() => handleOpenDialog(volunteer)} sx={{ mr: 1 }}>
                    Edit
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={openDialog} onClose={handleCloseDialog}>
        <DialogTitle>{editingVolunteer ? 'Edit Volunteer' : 'Add Volunteer'}</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label="Name"
            variant="outlined"
            value={name}
            onChange={(e) => setName(e.target.value)}
            sx={{ mt: 2 }}
          />
          <TextField
            fullWidth
            label="Email"
            variant="outlined"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            sx={{ mt: 2 }}
          />
          <TextField
            fullWidth
            label="Phone Number"
            variant="outlined"
            value={phoneNumber}
            onChange={(e) => setphoneNumber(e.target.value)}
            sx={{ mt: 2 }}
          />
          <Select
            fullWidth
            value={gender}
            onChange={(e) => setGender(Number(e.target.value))}
            sx={{ mt: 2 }}
          >
            <MenuItem value={1}>男</MenuItem>
            <MenuItem value={2}>女</MenuItem>
          </Select>
          {
            editingVolunteer?
              <Select
              fullWidth
              value={status}
              onChange={(e) => setStatus(Number(e.target.value))}
              sx={{ mt: 2 }}
            >
              <MenuItem value={1}>有効</MenuItem>
              <MenuItem value={0}>無効</MenuItem>
            </Select>: <></>
          }
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog} color="secondary">Cancel</Button>
          <Button onClick={handleSubmit} color="primary" variant="contained">
            {editingVolunteer ? 'Update' : 'Add'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default VolunteerManagement;
