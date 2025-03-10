import React, { useEffect, useState } from 'react';
import { MenuItem, Select, SelectChangeEvent, OutlinedInput, Chip, Box } from '@mui/material';
import request from '../utils/http';

interface Volunteer {
  id: number;
  name: string;
}

interface Props {
  value: string;
  onChange: (value: string) => void;
}

const VolunteerSelect: React.FC<Props> = ({ value, onChange }) => {
  const [volunteers, setVolunteers] = useState<Volunteer[]>([]);
  const [selectedIds, setSelectedIds] = useState<number[]>([]);

  useEffect(() => {
    const fetchVolunteers = async () => {
      try {
        const data = await request.get<Volunteer[]>('/volunteer');
        setVolunteers(data);
      } catch (error) {
        console.error('Failed to fetch volunteers', error);
      }
    };
    fetchVolunteers();
  }, []);

  useEffect(() => {
    const ids = value.split(',').map(id => parseInt(id)).filter(id => !isNaN(id));
    setSelectedIds(ids);
  }, [value]);

  const handleChange = (event: SelectChangeEvent<number[]>) => {
    const {
      target: { value },
    } = event;
    const valueArray = typeof value === 'string' ? value.split(',').map(v => parseInt(v)) : value;
    setSelectedIds(valueArray);
    onChange(valueArray.join(','));
  };

  return (
    <Select
      multiple
      value={selectedIds}
      onChange={handleChange}
      input={<OutlinedInput id="select-multiple-chip" label="ボランティア" />}
      sx={{ mt: 2 }}
      renderValue={(selected) => (
        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
          {selected.map((id) => {
            const volunteer = volunteers.find(vol => vol.id === id);
            return volunteer ? (
              <Chip key={id} label={volunteer.name} />
            ) : null;
          })}
        </Box>
      )}
      fullWidth
    >
      {volunteers.map((volunteer) => (
        <MenuItem key={volunteer.id} value={volunteer.id}>
          {volunteer.name}
        </MenuItem>
      ))}
    </Select>
  );
};

export default VolunteerSelect;
