import React, { useEffect, useState } from 'react';
import { TextField, MenuItem } from '@mui/material';
import request from '../utils/http';

interface Category {
  id: number;
  name: string;
}

interface Props {
  value: number;
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
}

const CategorySelect: React.FC<Props> = ({ value, onChange }) => {
  const [categories, setCategories] = useState<Category[]>([]);

  const fetchCategories = async () => {
    try {
      const data = await request.get<Category[]>('/issueType');
      setCategories(data);
    } catch (error) {
      console.error('Failed to fetch categories', error);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  return (
    <TextField
      select
      fullWidth
      label="タグ"
      value={value}
      onChange={onChange}
      variant="outlined"
      sx={{ mt: 2 }}
    >
      {categories
        .filter(category => !["Task", "Bug", "Request", "Other"].includes(category.name))
        .map((category) => (
        <MenuItem key={category.id} value={category.id}>
          {category.name}
        </MenuItem>
      ))}
    </TextField>
  );
};

export default CategorySelect;
