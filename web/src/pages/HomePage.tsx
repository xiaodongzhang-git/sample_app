import React, { useEffect } from 'react';
import { AppBar, Toolbar, Typography, Button, Drawer, List, ListItemButton, ListItemIcon, ListItemText } from '@mui/material';
import { Routes, Route, Link, useNavigate } from 'react-router-dom';
import ActivityManagement from '../pages/ActivityManagement';
import VolunteerManagement from '../pages/VolunteerManagement';
import TagManagement from '../pages/TagManagement';
import UserManagement from '../pages/UserManagement';
import EventIcon from '@mui/icons-material/Event';  // 确保已安装 @mui/icons-material
import PeopleIcon from '@mui/icons-material/People';
import LabelIcon from '@mui/icons-material/Label';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';

const HomePage: React.FC<{ onLogout: () => void }> = ({ onLogout }) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    console.log('Logging out...');
    onLogout();
    navigate('/login');
  };

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
    }
  }, [navigate]);

  return (
    <div>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" style={{ flexGrow: 1 }}>
            ボランティア活動管理システム
          </Typography>
          <Typography variant="body1" style={{ marginRight: 20 }}>
            ようこそ
          </Typography>
          <Button color="inherit" onClick={handleLogout}>Logout</Button>
        </Toolbar>
      </AppBar>
      <Drawer
        variant="permanent"
        anchor="left"
        sx={{
          width: 240,
          flexShrink: 0,
          '& .MuiDrawer-paper': {
            width: 240,
            boxSizing: 'border-box',
            marginTop: '64px',
            background: 'linear-gradient(180deg, #004ba0, #00bcd4)'
          },
        }}
      >
        <List>
          <ListItemButton component={Link} to="activity-management">
            <ListItemIcon><EventIcon /></ListItemIcon>
            <ListItemText primary="イベント" sx={{ color: 'white' }} />
          </ListItemButton>
          <ListItemButton component={Link} to="volunteer-management">
            <ListItemIcon><PeopleIcon /></ListItemIcon>
            <ListItemText primary="メンバー" sx={{ color: 'white' }} />
          </ListItemButton>
          <ListItemButton component={Link} to="tag-management">
            <ListItemIcon><LabelIcon /></ListItemIcon>
            <ListItemText primary="タグ" sx={{ color: 'white' }} />
          </ListItemButton>
          <ListItemButton component={Link} to="user-management">
            <ListItemIcon><AccountCircleIcon /></ListItemIcon>
            <ListItemText primary="ユーザー" sx={{ color: 'white' }} />
          </ListItemButton>
        </List>
      </Drawer>
      <main style={{ marginLeft: 250, padding: 20 }}>
        <Routes>
          <Route path="activity-management" element={<ActivityManagement />} />
          <Route path="volunteer-management" element={<VolunteerManagement />} />
          <Route path="tag-management" element={<TagManagement />} />
          <Route path="user-management" element={<UserManagement />} />
        </Routes>
      </main>
    </div>
  );
};

export default HomePage;
