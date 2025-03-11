// components/Register.tsx
'use client';
import React from 'react';
import { useRegister } from '@/app/hooks/useRegister';

const Register = () => {
  const { formData, handleChange, handleSubmit } = useRegister();

  return (
    <div className="max-w-md mx-auto mt-10 p-6 bg-black text-white rounded-lg shadow-md">
      <h1 className="text-2xl font-bold mb-6 text-center">Register</h1>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium mb-1">Email:</label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            className="w-full px-3 py-2 border border-gray-600 bg-gray-800 text-white rounded-md"
            required
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Password:</label>
          <input
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            className="w-full px-3 py-2 border border-gray-600 bg-gray-800 text-white rounded-md"
            required
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">First Name:</label>
          <input
            type="text"
            name="first_name"
            value={formData.first_name}
            onChange={handleChange}
            className="w-full px-3 py-2 border border-gray-600 bg-gray-800 text-white rounded-md"
            // required
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Last Name:</label>
          <input
            type="text"
            name="last_name"
            value={formData.last_name}
            onChange={handleChange}
            className="w-full px-3 py-2 border border-gray-600 bg-gray-800 text-white rounded-md"
            // required
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Address:</label>
          <input
            type="text"
            name="address"
            value={formData.address}
            onChange={handleChange}
            className="w-full px-3 py-2 border border-gray-600 bg-gray-800 text-white rounded-md"
            required
          />
        </div>
        <button
          type="submit"
          className="w-full bg-gray-800 text-white py-2 px-4 rounded-md hover:bg-gray-700 transition-colors border border-gray-600"
        >
          Register
        </button>
      </form>
    </div>
  );
};

export default Register;