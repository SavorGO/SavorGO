// hooks/useRegister.ts
import { useState } from 'react';
import { useRouter } from 'next/navigation';

export const useRegister = () => {
  const [formData, setFormData] = useState({
    "email": '',
    "password": '',
    "first_name": '',
    "last_name": '',
    "address": '',
  });

  const router = useRouter();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    console.log('Form Data:', formData);
    try {
        const response = await fetch('http://localhost:8080/api/authentication/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData),
        });

      if (response.ok) {
        router.push('/login');
      } else {
        const errorData = await response.json();
        console.error('Registration failed:', errorData.message);
      }
    } catch (error) {
      console.error('Registration failed:', error);
    }
  };

  return {
    formData,
    handleChange,
    handleSubmit,
  };
};