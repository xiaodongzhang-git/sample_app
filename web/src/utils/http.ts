import axios from 'axios';
import { SnackbarContext } from '../App';
import React from 'react';

const http = axios.create({
  baseURL: 'http://localhost:9000',
  timeout: 10000,
});

http.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

http.interceptors.response.use(
  (response) => {
    const res = response.data;
    if (res.code === 200) {
      return res.data;
    } else {
      const showSnackbar = React.useContext(SnackbarContext)?.showSnackbar;
      if (res.code === 401) {
        showSnackbar?.('Token is invalid, please log in again', 'error');
        window.location.href = '/login';
        return Promise.reject(new Error('Token Expiration'));
      } else {
        showSnackbar?.(res.message || 'Request failed', 'error');
        return Promise.reject(new Error(res.message || 'Error'));
      }
    }
  },
  (error) => {
    const showSnackbar = React.useContext(SnackbarContext)?.showSnackbar;
    showSnackbar?.('Server error, please try again later', 'error');
    return Promise.reject(error);
  }
);

const request = {
  get: <T = any>(url: string, params?: object): Promise<T> => {
    return http.get(url, { params });
  },
  post: <T = any>(url: string, data?: object): Promise<T> => {
    return http.post(url, data);
  },
  put: <T = any>(url: string, data?: object): Promise<T> => {
    return http.put(url, data);
  },
  delete: <T = any>(url: string, params?: object): Promise<T> => {
    return http.delete(url, { params });
  },
};

export default request;
