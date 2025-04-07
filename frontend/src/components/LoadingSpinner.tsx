type LoadingSpinnerProps = {
  message?: string;
};

const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({ message = "로딩 중..." }) => {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-white">
      <div className="text-center">
        <div className="mb-4">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900 mx-auto"></div>
        </div>
        <p className="text-lg font-medium text-gray-600">{message}</p>
      </div>
    </div>
  );
};

export default LoadingSpinner;