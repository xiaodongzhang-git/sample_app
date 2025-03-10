import React, { useEffect, useState, useContext } from 'react';
import {
  Container, Typography, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Paper, Button, Dialog, DialogActions, DialogContent,
  DialogTitle, TextField
} from '@mui/material';
import request from '../utils/http';
import { SnackbarContext } from '../App';

interface Category {
  id: number;
  name: string;
}

const TagManagement: React.FC = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [openDialog, setOpenDialog] = useState(false);
  const [editingCategory, setEditingCategory] = useState<Category | null>(null);
  const [categoryName, setCategoryName] = useState('');
  const { showSnackbar } = useContext(SnackbarContext) || {};

  const fetchCategories = async () => {
    try {
      const data = await request.get<Category[]>('/issueType');
      setCategories(data);
    } catch (error) {
      console.log(error);
      showSnackbar?.('Failed to obtain the tag list', 'error');
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);


  const handleOpenDialog = (category: Category | null = null) => {
    setEditingCategory(category);
    setCategoryName(category ? category.name : '');
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setEditingCategory(null);
    setCategoryName('');
  };

  const handleSubmit = async () => {
    if (!categoryName.trim()) {
      showSnackbar?.('タグ名を入力してください', 'warning');
      return;
    }

    try {
      if (editingCategory) {
        await request.put(`/issueType/${editingCategory.id}`, { name: categoryName });
        showSnackbar?.('タグが正常に更新されました', 'success');
      } else {
        await request.post('/issueType', { name: categoryName });
        showSnackbar?.('タグが正常に作成されました', 'success');
      }
      fetchCategories();
      handleCloseDialog();
    } catch (error) {
      showSnackbar?.('Operation failed', 'error');
    }
  };

  return (
    <Container>
      <Typography variant="h4" gutterBottom>
        タグ管理
      </Typography>
      <Button variant="contained" color="primary" onClick={() => handleOpenDialog(null)} sx={{ mb: 2 }}>
        タグを追加
      </Button>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>タグ名</TableCell>
              <TableCell align="right">操作</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {categories
                .filter(category => !["Task", "Bug", "Request", "Other"].includes(category.name))
                .map((category) => (
              <TableRow key={category.id}>
                <TableCell>{category.id}</TableCell>
                <TableCell>{category.name}</TableCell>
                <TableCell align="right">
                  <Button variant="outlined" color="primary" onClick={() => handleOpenDialog(category)} sx={{ mr: 1 }}>
                    編集
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={openDialog} onClose={handleCloseDialog}>
        <DialogTitle>{editingCategory ? 'タグを編集' : 'タグを追加'}</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label="タグ名"
            variant="outlined"
            value={categoryName}
            onChange={(e) => setCategoryName(e.target.value)}
            sx={{ mt: 2 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog} color="secondary">キャンセル</Button>
          <Button onClick={handleSubmit} color="primary" variant="contained">
            {editingCategory ? '更新' : '追加'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default TagManagement;
