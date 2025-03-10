import React, { useEffect, useState, useContext } from 'react';
import {
  Container, Typography, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Paper, Button, Dialog, DialogActions, DialogContent,
  DialogTitle, TextField
} from '@mui/material';
import request from '../utils/http';
import { SnackbarContext } from '../App';
import CategorySelect from '../components/CategorySelect';
import VolunteerSelect from '../components/VolunteerSelect';

interface Activity {
  id: number;
  issueTypeId: number;
  issueTypeName: string;
  summary: string;
  description: string;
  startDate: string;
  dueDate: string;
  volunteerIds: string;
  wikiContent: string;
}

const ActivityManagement: React.FC = () => {
  const [activities, setActivities] = useState<Activity[]>([]);
  const [openDialog, setOpenDialog] = useState(false);
  const [editingActivity, setEditingActivity] = useState<Activity | null>(null);
  const [summary, setSummary] = useState('');
  const [issueTypeId, setIssueTypeId] = useState(0);
  const [issueTypeName, setIssueTypeName] = useState('');
  const [description, setDescription] = useState('');
  const [startDate, setStartDate] = useState('');
  const [dueDate, setDueDate] = useState('');
  const { showSnackbar } = useContext(SnackbarContext) || {};
  const [volunteerIds, setSelectedVolunteerIds] = useState('');
  const [wikiContent, setWikiContent] = useState('');

  const handleCategoryChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIssueTypeId(Number(event.target.value));
  };


  const handleVolunteersChange = (ids: string) => {
    setSelectedVolunteerIds(ids);
  };

  const fetchActivities = async () => {
    try {
      const data = await request.get<Activity[]>('/issue');
      setActivities(data);
    } catch (error) {
      console.log(error);
      showSnackbar?.('Failed to get the activity list', 'error');
    }
  };

  useEffect(() => {
    fetchActivities();
  }, []);

  const handleOpenDialog = (activity: Activity | null = null) => {
    setEditingActivity(activity);
    setSummary(activity ? activity.summary : '');
    setDescription(activity ? activity.description : '');
    setStartDate(activity ? activity.startDate.split('T')[0] : '');
    setDueDate(activity ? activity.dueDate.split('T')[0] : '');
    setIssueTypeId(activity ? activity.issueTypeId : 0);
    setIssueTypeName(activity ? activity.issueTypeName : '');
    setSelectedVolunteerIds(activity ? activity.volunteerIds : '');
    setWikiContent(activity ? activity.wikiContent : '');
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setEditingActivity(null);
    setSummary('');
    setDescription('');
    setStartDate('');
    setDueDate('');
    setIssueTypeId(0);
    setIssueTypeName('');
    setSelectedVolunteerIds('');
    setWikiContent('');
  };

  const handleSubmit = async () => {
    if (!summary.trim() || !description.trim() || !startDate.trim() || !dueDate.trim() || !issueTypeId) {
      showSnackbar?.('すべての欄にご記入ください', 'warning');
      return;
    }

    const payload = {
      summary,
      issueTypeId,
      description,
      startDate,
      dueDate,
      wikiContent,
      volunteerIds
    };

    try {
      if (editingActivity) {
        await request.put(`/issue/${editingActivity.id}`, payload);
        showSnackbar?.('正常に更新されました', 'success');
      } else {
        await request.post('/issue', payload);
        showSnackbar?.('正常に作成されました', 'success');
      }
      fetchActivities();
      handleCloseDialog();
    } catch (error) {
      showSnackbar?.('Operation failed', 'error');
    }
  };

  return (
    <Container>
      <Typography variant="h4" gutterBottom>
        イベント管理
      </Typography>
      <Button variant="contained" color="primary" onClick={() => handleOpenDialog(null)} sx={{ mb: 2 }}>
        イベントを追加
      </Button>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>タグ</TableCell>
              <TableCell>概要</TableCell>
              <TableCell>説明</TableCell>
              <TableCell>記録</TableCell>
              <TableCell>開始日</TableCell>
              <TableCell>終了日</TableCell>
              <TableCell align="right">操作</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {activities
              .filter(ac => !["Task", "Bug", "Request", "Other"].includes(ac.issueTypeName))
              .map((activity) => (
                <TableRow key={activity.id}>
                  <TableCell>{activity.id}</TableCell>
                  <TableCell sx={{ width: '100px' }}>{activity.issueTypeName}</TableCell>
                  <TableCell>{activity.summary}</TableCell>
                  <TableCell title={activity.description} sx={{ maxWidth: '150px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                    {activity.description.slice(0, 50) + (activity.description.length > 50 ? '...' : '')}
                  </TableCell>
                  <TableCell title={activity.wikiContent} sx={{ maxWidth: '150px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                    {activity.wikiContent.slice(0, 50) + (activity.wikiContent.length > 50 ? '...' : '')}
                  </TableCell>
                  <TableCell>{activity.startDate.split('T')[0]}</TableCell>
                  <TableCell>{activity.dueDate.split('T')[0]}</TableCell>
                  <TableCell align="right">
                    <Button variant="outlined" color="primary" onClick={() => handleOpenDialog(activity)} sx={{ mr: 1 }}>
                      編集
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={openDialog} onClose={handleCloseDialog}>
        <DialogTitle>{editingActivity ? 'イベントを編集' : 'イベントを追加する'}</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label="概要"
            variant="outlined"
            value={summary}
            onChange={(e) => setSummary(e.target.value)}
            sx={{ mt: 2 }}
          />
          <TextField
            fullWidth
            label="説明"
            variant="outlined"
            multiline
            rows={4}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            sx={{ mt: 2 }}
          />
          <TextField
            fullWidth
            label="開始日"
            type="date"
            InputLabelProps={{ shrink: true }}
            variant="outlined"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            sx={{ mt: 2 }}
          />
          <TextField
            fullWidth
            label="終了日"
            type="date"
            InputLabelProps={{ shrink: true }}
            variant="outlined"
            value={dueDate}
            onChange={(e) => setDueDate(e.target.value)}
            sx={{ mt: 2 }}
          />
          <CategorySelect value={issueTypeId} onChange={handleCategoryChange}/>
          <VolunteerSelect value={volunteerIds} onChange={handleVolunteersChange} />
          <TextField
            fullWidth
            label="記録"
            variant="outlined"
            multiline
            rows={10}
            value={wikiContent}
            onChange={(e) => setWikiContent(e.target.value)}
            sx={{ mt: 2 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog} color="secondary">取消</Button>
          <Button onClick={handleSubmit} color="primary" variant="contained">
            {editingActivity ? '更新' : '添加'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default ActivityManagement;
