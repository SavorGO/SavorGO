import React from 'react';
import { CartDetailsType } from '@/lib/interface';

interface CheckOutPageProps {
  formFields: any[];
  total: number;
  cartDetails: CartDetailsType[];
}

const CheckOutPage: React.FC<CheckOutPageProps> = ({ formFields, total, cartDetails }) => {
  return (
    <div className="max-w-2xl mx-auto p-6 bg-white rounded-lg shadow-md">
      <h2 className="text-2xl font-semibold mb-4">Checkout</h2>
      <div className="space-y-4">
        {cartDetails.length > 0 ? (
          cartDetails.map((item) => (
            <div key={item.id} className="flex justify-between border-b pb-2">
              <span>{item.name} (x{item.quantity})</span>
              <span>${item.price.toFixed(2)}</span>
            </div>
          ))
        ) : (
          <p className="text-gray-500">Your cart is empty.</p>
        )}
      </div>
      <div className="mt-4 flex justify-between font-semibold text-lg">
        <span>Total:</span>
        <span>${total.toFixed(2)}</span>
      </div>
      <form className="mt-6 space-y-4">
        {formFields.map((field, index) => (
          <div key={index}>
            <label className="block text-sm font-medium text-gray-700">{field.label}</label>
            <input
              type={field.type || 'text'}
              className="mt-1 block w-full p-2 border border-gray-300 rounded-md shadow-sm"
            />
          </div>
        ))}
        <button
          type="submit"
          className="w-full mt-4 bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700"
        >
          Place Order
        </button>
      </form>
    </div>
  );
};

export default CheckOutPage;
