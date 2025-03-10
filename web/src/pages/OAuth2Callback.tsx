import React, { useEffect, useRef } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import request from '../utils/http';

const OAuth2Callback = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const hasFetched = useRef(false);

  useEffect(() => {
    if (hasFetched.current) return;
    hasFetched.current = true;

    const fetchToken = async () => {
      const code = searchParams.get('code');
      const state = searchParams.get('state');

      if (code && state === '001') {
        try {
          await request.post('/backlog/token', { code });
          navigate('/home');
        } catch (error) {
          console.error("Token fetch failed", error);
          navigate('/login', { replace: true });
        }
      } else {
        navigate('/login', { replace: true });
      }
    };

    fetchToken();
  }, [searchParams, navigate]);

  return <div>Loading...</div>;
};

export default OAuth2Callback;
